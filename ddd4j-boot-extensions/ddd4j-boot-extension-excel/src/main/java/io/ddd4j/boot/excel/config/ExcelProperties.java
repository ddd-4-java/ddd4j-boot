package io.ddd4j.boot.excel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ddd4j-boot-excel 配置属性（绑定 {@code ddd4j.excel.*}）。
 *
 * <p>位于 boot 侧（依赖 Spring Boot）；库侧 {@code ddd4j-extension-excel} 不含此类，
 * 以保持库侧"纯 Java 无 Spring"的设计约束。
 *
 * <p>配置示例（application.yml）：
 * <pre>{@code
 * ddd4j:
 *   excel:
 *     enabled: true
 *     batch-size: 1000
 *     max-upload-mb: 50
 *     charset: UTF-8
 *     style:
 *       default-border: true
 *       auto-size-column: true
 *       header-row-height: 600
 * }</pre>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@ConfigurationProperties(prefix = "ddd4j.excel")
public class ExcelProperties {

    /**
     * 配置前缀。
     */
    public static final String PREFIX = "ddd4j.excel";

    /**
     * 是否启用 ddd4j-boot-excel 自动装配（总开关，默认 true）。
     */
    private boolean enabled = true;

    /**
     * 监听器批量入库阈值，默认 1000 行。
     */
    private int batchSize = 1000;

    /**
     * Web 上传单文件大小上限（MB），默认 50。
     */
    private int maxUploadMB = 50;

    /**
     * 字符编码，默认 UTF-8（影响 CSV 与文件名编码）。
     */
    private String charset = "UTF-8";

    /**
     * 样式相关配置。
     */
    private Style style = new Style();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    public int getMaxUploadMB() { return maxUploadMB; }
    public void setMaxUploadMB(int maxUploadMB) { this.maxUploadMB = maxUploadMB; }
    public String getCharset() { return charset; }
    public void setCharset(String charset) { this.charset = charset; }
    public Style getStyle() { return style; }
    public void setStyle(Style style) { this.style = style; }

    public static class Style {

        /**
         * 是否为单元格启用默认细边框。
         */
        private boolean defaultBorder = true;

        /**
         * 是否自动适配列宽（基于内容长度）。
         */
        private boolean autoSizeColumn = true;

        /**
         * 表头行高（单位：1/20 磅，即 short）。
         * <p>例如 600 表示 30 磅；默认 600。
         */
        private short headerRowHeight = 600;

        public boolean isDefaultBorder() { return defaultBorder; }
        public void setDefaultBorder(boolean defaultBorder) { this.defaultBorder = defaultBorder; }
        public boolean isAutoSizeColumn() { return autoSizeColumn; }
        public void setAutoSizeColumn(boolean autoSizeColumn) { this.autoSizeColumn = autoSizeColumn; }
        public short getHeaderRowHeight() { return headerRowHeight; }
        public void setHeaderRowHeight(short headerRowHeight) { this.headerRowHeight = headerRowHeight; }
    }
}
