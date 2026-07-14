package io.ddd4j.boot.qrcode;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** QR code service and HTTP delivery settings. */
@Data
@ConfigurationProperties(prefix = QrCodeProperties.PREFIX)
public class QrCodeProperties {

    public static final String PREFIX = "ddd4j.qrcode";

    private boolean enabled = true;
    private int concurrency = Math.min(Runtime.getRuntime().availableProcessors(), 8);
    private int maxBatchSize = 100;
    private int maxUploadBytes = 10 * 1024 * 1024;
    private final Web web = new Web();

    @Data
    public static class Web {

        private boolean enabled;
    }
}
