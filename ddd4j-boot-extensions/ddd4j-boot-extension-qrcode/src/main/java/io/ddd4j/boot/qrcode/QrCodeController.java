package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.QrCodeService;
import io.ddd4j.extension.qrcode.batch.QrCodeBatchItem;
import io.ddd4j.extension.qrcode.batch.QrCodeBatchItemResult;
import io.ddd4j.extension.qrcode.batch.QrCodeBatchResult;
import io.ddd4j.extension.qrcode.command.DecodeQrCodeCommand;
import io.ddd4j.extension.qrcode.command.GenerateQrCodeCommand;
import io.ddd4j.extension.qrcode.model.QrCodeDecodeRequest;
import io.ddd4j.extension.qrcode.model.QrCodeRequest;
import io.ddd4j.extension.qrcode.result.QrCodeArtifact;
import io.ddd4j.extension.qrcode.result.QrCodeScanResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * 二维码 HTTP 端点控制器（按需开启，由 {@link QrCodeProperties} 的 web 开关控制注册）。
 *
 * <p>提供渲染、解码与批量渲染三类端点；远程 URL 解码有意不支持。
 */
@RestController
@RequestMapping("/qrcodes")
public class QrCodeController {

    private static final String PNG_DATA_URI_PREFIX = "data:image/png;base64,";

    private final QrCodeService service;
    private final QrCodeProperties properties;

    /**
     * 以二维码服务与配置属性构造控制器。
     *
     * @param service    二维码生成/解码服务，不允许为 {@code null}
     * @param properties 二维码配置属性（上传大小上限等），不允许为 {@code null}
     * @throws NullPointerException 任一参数为 {@code null} 时抛出
     */
    public QrCodeController(QrCodeService service, QrCodeProperties properties) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    /**
     * 渲染二维码：接收 JSON 内容并返回 PNG 图像字节。
     *
     * @param input 渲染请求（内容、宽高、关联标识），不允许为 {@code null}
     * @return 包含 PNG 字节与 {@code image/png} 内容类型的响应实体
     * @throws NullPointerException 请求为 {@code null} 时抛出
     */
    @PostMapping(value = "/render", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> render(@RequestBody RenderRequest input) {
        Objects.requireNonNull(input, "render request must not be null");
        QrCodeArtifact artifact = service.generate(GenerateQrCodeCommand.builder()
                .correlationId(input.getCorrelationId())
                .request(toRequest(input))
                .build());
        // 上游 QrCodeService 统一输出 PNG bytes
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(artifact.getOutput().getBytes());
    }

    /**
     * 解码上传的二维码图像（multipart 上传，不支持远程 URL 解码）。
     *
     * @param file 二维码图像文件，不允许为 {@code null}
     * @return 解码扫描结果
     * @throws IOException                  读取上传文件字节失败时抛出
     * @throws IllegalArgumentException     文件大小超过 {@code ddd4j.qrcode.max-upload-bytes} 配置上限时抛出
     * @throws NullPointerException         文件为 {@code null} 时抛出
     */
    @PostMapping(value = "/decode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public QrCodeScanResult decode(@RequestPart("file") MultipartFile file) throws IOException {
        Objects.requireNonNull(file, "file must not be null");
        if (file.getSize() > properties.getMaxUploadBytes()) {
            throw new IllegalArgumentException("QR code image exceeds configured upload limit");
        }
        return service.decode(DecodeQrCodeCommand.builder()
                .request(QrCodeDecodeRequest.from(file.getBytes()))
                .build());
    }

    /**
     * 批量渲染二维码：逐条生成并返回每项的成功状态与 PNG data URI。
     *
     * @param inputs 批量渲染请求列表，不允许为 {@code null}，元素及其 request 亦不允许为 {@code null}
     * @return 与入项顺序一致的批量响应列表；失败项仅携带错误码与错误信息
     * @throws NullPointerException 入列表或任一元素为 {@code null} 时抛出
     */
    @PostMapping(value = "/batch", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BatchItemResponse> batch(@RequestBody List<BatchRenderRequest> inputs) {
        Objects.requireNonNull(inputs, "batch requests must not be null");
        List<QrCodeBatchItem> items = new ArrayList<>(inputs.size());
        for (BatchRenderRequest input : inputs) {
            Objects.requireNonNull(input, "batch request must not be null");
            Objects.requireNonNull(input.getRequest(), "batch render request must not be null");
            items.add(QrCodeBatchItem.builder()
                    .itemId(input.getItemId())
                    .command(GenerateQrCodeCommand.builder()
                            .correlationId(input.getRequest().getCorrelationId())
                            .request(toRequest(input.getRequest()))
                            .build())
                    .build());
        }
        QrCodeBatchResult result = service.generateBatch(items);
        List<BatchItemResponse> response = new ArrayList<>(result.getItems().size());
        for (QrCodeBatchItemResult item : result.getItems()) {
            response.add(new BatchItemResponse(item.getItemId(), item.isSuccess(),
                    item.isSuccess() ? dataUri(item.getArtifact()) : null,
                    item.getErrorCode(), item.getErrorMessage()));
        }
        return response;
    }

    /**
     * 将渲染请求转换为库侧二维码请求；宽高非正数时回退到默认 300×300。
     *
     * @param input 渲染请求，不允许为 {@code null}
     * @return 库侧 {@link QrCodeRequest}
     */
    private QrCodeRequest toRequest(RenderRequest input) {
        Objects.requireNonNull(input, "render request must not be null");
        int width = input.getWidth() > 0 ? input.getWidth() : 300;
        int height = input.getHeight() > 0 ? input.getHeight() : 300;
        return new QrCodeRequest(input.getContent(), width, height);
    }

    /**
     * 将二维码产物编码为 PNG data URI（Base64 内联）。
     *
     * @param artifact 二维码产物，不允许为 {@code null}
     * @return 形如 {@code data:image/png;base64,...} 的 data URI 字符串
     */
    private String dataUri(QrCodeArtifact artifact) {
        return PNG_DATA_URI_PREFIX + Base64.getEncoder().encodeToString(artifact.getOutput().getBytes());
    }

    /**
     * 单个二维码渲染的 HTTP 请求体（内容、宽高、关联标识）。
     */
    public static class RenderRequest {

        /** 关联标识，用于链路追踪与幂等关联。 */
        private String correlationId;
        /** 待编码为二维码的文本内容。 */
        private String content;
        /** 二维码宽度（像素），非正数时回退默认 256（服务端换算为 300）。 */
        private int width = 256;
        /** 二维码高度（像素），非正数时回退默认 256（服务端换算为 300）。 */
        private int height = 256;

        /**
         * 显式无参构造器，供 JSON 反序列化（Jackson）实例化请求体。
         */
        public RenderRequest() {
        }

        /** 获取关联标识。
         * @return 关联标识 */
        public String getCorrelationId() {
            return correlationId;
        }

        /** 设置关联标识。
         * @param correlationId 关联标识 */
        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        /** 获取待编码文本内容。
         * @return 文本内容 */
        public String getContent() {
            return content;
        }

        /** 设置待编码文本内容。
         * @param content 文本内容 */
        public void setContent(String content) {
            this.content = content;
        }

        /** 获取二维码宽度。
         * @return 宽度（像素） */
        public int getWidth() {
            return width;
        }

        /** 设置二维码宽度。
         * @param width 宽度（像素） */
        public void setWidth(int width) {
            this.width = width;
        }

        /** 获取二维码高度。
         * @return 高度（像素） */
        public int getHeight() {
            return height;
        }

        /** 设置二维码高度。
         * @param height 高度（像素） */
        public void setHeight(int height) {
            this.height = height;
        }
    }

    /**
     * 批量渲染中的单条请求项（业务项标识 + 渲染请求）。
     */
    public static class BatchRenderRequest {

        /** 批量项业务标识，用于响应对齐。 */
        private String itemId;
        /** 该项对应的渲染请求。 */
        private RenderRequest request;

        /**
         * 显式无参构造器，供 JSON 反序列化（Jackson）实例化请求项。
         */
        public BatchRenderRequest() {
        }

        /** 获取批量项标识。
         * @return 批量项标识 */
        public String getItemId() {
            return itemId;
        }

        /** 设置批量项标识。
         * @param itemId 批量项标识 */
        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        /** 获取渲染请求。
         * @return 渲染请求 */
        public RenderRequest getRequest() {
            return request;
        }

        /** 设置渲染请求。
         * @param request 渲染请求 */
        public void setRequest(RenderRequest request) {
            this.request = request;
        }
    }

    /**
     * 批量渲染的单条响应项：成功时携带 data URI，失败时携带错误码与错误信息。
     */
    public static class BatchItemResponse {

        /** 批量项业务标识，与请求项一一对应。 */
        private final String itemId;
        /** 是否生成成功。 */
        private final boolean success;
        /** 成功时的 PNG data URI，失败时为 {@code null}。 */
        private final String dataUri;
        /** 失败时的错误码，成功时为 {@code null}。 */
        private final String errorCode;
        /** 失败时的错误信息，成功时为 {@code null}。 */
        private final String errorMessage;

        /**
         * 以批量项执行结果构造响应项。
         *
         * @param itemId       批量项业务标识
         * @param success      是否生成成功
         * @param dataUri      成功时的 PNG data URI，可为 {@code null}
         * @param errorCode    失败时的错误码，可为 {@code null}
         * @param errorMessage 失败时的错误信息，可为 {@code null}
         */
        public BatchItemResponse(String itemId, boolean success, String dataUri,
                String errorCode, String errorMessage) {
            this.itemId = itemId;
            this.success = success;
            this.dataUri = dataUri;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /** 获取批量项标识。
         * @return 批量项标识 */
        public String getItemId() {
            return itemId;
        }

        /** 是否生成成功。
         * @return 成功返回 {@code true} */
        public boolean isSuccess() {
            return success;
        }

        /** 获取成功时的 data URI。
         * @return PNG data URI，失败时为 {@code null} */
        public String getDataUri() {
            return dataUri;
        }

        /** 获取失败错误码。
         * @return 错误码，成功时为 {@code null} */
        public String getErrorCode() {
            return errorCode;
        }

        /** 获取失败错误信息。
         * @return 错误信息，成功时为 {@code null} */
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
