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

/** Opt-in QR code HTTP endpoints. Remote URL decoding is intentionally unsupported. */
@RestController
@RequestMapping("/qrcodes")
public class QrCodeController {

    private static final String PNG_DATA_URI_PREFIX = "data:image/png;base64,";

    private final QrCodeService service;
    private final QrCodeProperties properties;

    public QrCodeController(QrCodeService service, QrCodeProperties properties) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

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

    private QrCodeRequest toRequest(RenderRequest input) {
        Objects.requireNonNull(input, "render request must not be null");
        int width = input.getWidth() > 0 ? input.getWidth() : 300;
        int height = input.getHeight() > 0 ? input.getHeight() : 300;
        return new QrCodeRequest(input.getContent(), width, height);
    }

    private String dataUri(QrCodeArtifact artifact) {
        return PNG_DATA_URI_PREFIX + Base64.getEncoder().encodeToString(artifact.getOutput().getBytes());
    }

    public static class RenderRequest {

        private String correlationId;
        private String content;
        private int width = 256;
        private int height = 256;

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static class BatchRenderRequest {

        private String itemId;
        private RenderRequest request;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public RenderRequest getRequest() {
            return request;
        }

        public void setRequest(RenderRequest request) {
            this.request = request;
        }
    }

    public static class BatchItemResponse {

        private final String itemId;
        private final boolean success;
        private final String dataUri;
        private final String errorCode;
        private final String errorMessage;

        public BatchItemResponse(String itemId, boolean success, String dataUri,
                String errorCode, String errorMessage) {
            this.itemId = itemId;
            this.success = success;
            this.dataUri = dataUri;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getItemId() {
            return itemId;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getDataUri() {
            return dataUri;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
