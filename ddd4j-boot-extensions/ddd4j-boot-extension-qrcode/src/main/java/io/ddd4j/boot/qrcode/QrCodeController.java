package io.ddd4j.boot.qrcode;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.ddd4j.extension.qrcode.QrCodeService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        QrCodeImageFormat format = QrCodeImageFormat.valueOf(input.getFormat().toUpperCase());
        QrCodeRequest request = QrCodeRequest.builder(input.getContent())
                .size(input.getWidth(), input.getHeight())
                .margin(input.getMargin())
                .errorCorrectionLevel(ErrorCorrectionLevel.valueOf(input.getErrorCorrectionLevel().toUpperCase()))
                .format(format)
                .selfCheck(input.isSelfCheck())
                .build();
        QrCodeArtifact artifact = service.generate(GenerateQrCodeCommand.builder()
                .correlationId(input.getCorrelationId())
                .request(request)
                .build());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(artifact.getOutput().getFormat().getMimeType()))
                .body(artifact.getOutput().getBytes());
    }

    @PostMapping(value = "/decode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public QrCodeScanResult decode(@RequestPart("file") MultipartFile file) throws IOException {
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
}
