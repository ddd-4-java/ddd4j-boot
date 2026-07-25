package io.ddd4j.boot.qrcode;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** QR code service and HTTP delivery settings. */
@ConfigurationProperties(prefix = QrCodeProperties.PREFIX)
public class QrCodeProperties {

    public static final String PREFIX = "ddd4j.qrcode";

    private boolean enabled = true;
    private int concurrency = Math.min(Runtime.getRuntime().availableProcessors(), 8);
    private int maxBatchSize = 100;
    private int maxUploadBytes = 10 * 1024 * 1024;
    private final Web web = new Web();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public int getMaxUploadBytes() {
        return maxUploadBytes;
    }

    public void setMaxUploadBytes(int maxUploadBytes) {
        this.maxUploadBytes = maxUploadBytes;
    }

    public Web getWeb() {
        return web;
    }

    public static class Web {

        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}