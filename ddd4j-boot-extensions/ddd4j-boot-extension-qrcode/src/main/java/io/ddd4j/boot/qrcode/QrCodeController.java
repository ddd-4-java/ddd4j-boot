package io.ddd4j.boot.qrcode;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.ddd4j.extension.qrcode.QrCodeService;
import io.ddd4j.extension.qrcode.batch.QrCodeBatchItem;
import io.ddd4j.extension.qrcode.batch.QrCodeBatchItemResult;
import io.ddd4j.extension.qrcode.batch.QrCodeBatchResult;
import io.ddd4j.extension.qrcode.command.DecodeQrCodeCommand;
import io.ddd4j.extension.qrcode.command.GenerateQrCodeCommand;
import io.ddd4j.extension.qrcode.result.QrCodeArtifact;
import io.ddd4j.extension.qrcode.result.QrCodeScanResult;
import io.github.hiwepy.zxing.model.QrCodeDecodeRequest;
import io.github.hiwepy.zxing.model.QrCodeImageFormat;
import io.github.hiwepy.zxing.model.QrCodeRequest;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Opt-in QR code HTTP endpoints. Remote URL decoding is intentionally unsupported. */
@RestController
@RequestMapping("/qrcodes")
public class QrCodeController {

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
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(artifact.getOutput().getFormat().getMimeType()))
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
                .request(QrCodeDecodeRequest.from(file.getBytes())
                        .multiple(true)
                        .maxInputBytes(properties.getMaxUploadBytes())
                        .build())
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
                    item.isSuccess() ? item.getArtifact().getOutput().dataUri() : null,
                    item.getErrorCode(), item.getErrorMessage()));
        }
        return response;
    }

    private QrCodeRequest toRequest(RenderRequest input) {
        Objects.requireNonNull(input, "render request must not be null");
        String formatValue = StringUtils.hasText(input.getFormat()) ? input.getFormat() : "PNG";
        String errorLevelValue = StringUtils.hasText(input.getErrorCorrectionLevel())
                ? input.getErrorCorrectionLevel() : "M";
        QrCodeImageFormat format = QrCodeImageFormat.valueOf(formatValue.toUpperCase());
        return QrCodeRequest.builder(input.getContent())
                .size(input.getWidth(), input.getHeight())
                .margin(input.getMargin())
                .errorCorrectionLevel(ErrorCorrectionLevel.valueOf(errorLevelValue.toUpperCase()))
                .format(format)
                .selfCheck(input.isSelfCheck())
                .build();
    }

    @Data
    public static class RenderRequest {

        private String correlationId;
        private String content;
        private int width = 256;
        private int height = 256;
        private int margin = 2;
        private String format = "PNG";
        private String errorCorrectionLevel = "M";
        private boolean selfCheck;
    }

    @Data
    public static class BatchRenderRequest {

        private String itemId;
        private RenderRequest request;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class BatchItemResponse {

        private final String itemId;
        private final boolean success;
        private final String dataUri;
        private final String errorCode;
        private final String errorMessage;
    }
}
