package io.ddd4j.boot.qrcode;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 二维码服务与 HTTP 投递配置属性（绑定 {@code ddd4j.qrcode.*}）。
 *
 * <p>控制总开关、生成并发度、批量上限、上传解码字节上限以及 Web 端点开关。
 */
@ConfigurationProperties(prefix = QrCodeProperties.PREFIX)
public class QrCodeProperties {

    /**
     * 配置属性前缀 {@code ddd4j.qrcode}。
     */
    public static final String PREFIX = "ddd4j.qrcode";

    /** 是否启用二维码扩展（总开关，默认 true）。 */
    private boolean enabled = true;
    /** 生成并发度，默认取 CPU 核数与 8 的较小值。 */
    private int concurrency = Math.min(Runtime.getRuntime().availableProcessors(), 8);
    /** 单次批量渲染的最大条数，默认 100。 */
    private int maxBatchSize = 100;
    /** 上传解码的单文件字节上限，默认 10 MiB。 */
    private int maxUploadBytes = 10 * 1024 * 1024;
    /** Web 投递相关配置。 */
    private final Web web = new Web();

    /**
     * 显式无参构造器，供 Spring 绑定 {@code ddd4j.qrcode.*} 配置。
     */
    public QrCodeProperties() {
    }

    /** 是否启用二维码扩展。
     * @return 启用返回 {@code true} */
    public boolean isEnabled() {
        return enabled;
    }

    /** 设置是否启用二维码扩展。
     * @param enabled 是否启用 */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** 获取生成并发度。
     * @return 并发度 */
    public int getConcurrency() {
        return concurrency;
    }

    /** 设置生成并发度。
     * @param concurrency 并发度 */
    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    /** 获取单次批量渲染最大条数。
     * @return 最大条数 */
    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    /** 设置单次批量渲染最大条数。
     * @param maxBatchSize 最大条数 */
    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    /** 获取上传解码的单文件字节上限。
     * @return 字节上限 */
    public int getMaxUploadBytes() {
        return maxUploadBytes;
    }

    /** 设置上传解码的单文件字节上限。
     * @param maxUploadBytes 字节上限 */
    public void setMaxUploadBytes(int maxUploadBytes) {
        this.maxUploadBytes = maxUploadBytes;
    }

    /** 获取 Web 投递配置（只读嵌套对象）。
     * @return Web 配置 */
    public Web getWeb() {
        return web;
    }

    /**
     * Web 投递相关配置（嵌套属性 {@code ddd4j.qrcode.web.*}）。
     */
    public static class Web {

        /** 是否启用二维码 HTTP 端点（默认 false，按需开启）。 */
        private boolean enabled;

        /**
         * 显式无参构造器，供 Spring 绑定嵌套配置。
         */
        public Web() {
        }

        /** 是否启用二维码 HTTP 端点。
         * @return 启用返回 {@code true} */
        public boolean isEnabled() {
            return enabled;
        }

        /** 设置是否启用二维码 HTTP 端点。
         * @param enabled 是否启用 */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}