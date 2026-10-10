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

    /**
     * 显式无参构造器，供 Spring 以自动装配方式绑定 {@code ddd4j.excel.*} 配置。
     */
    public ExcelProperties() {
    }

    /** 是否启用 ddd4j-boot-excel 自动装配。
     * @return 启用返回 {@code true} */
    public boolean isEnabled() { return enabled; }
    /** 设置总开关。
     * @param enabled 是否启用 */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    /** 获取监听器批量入库阈值。
     * @return 行数阈值 */
    public int getBatchSize() { return batchSize; }
    /** 设置监听器批量入库阈值。
     * @param batchSize 行数阈值 */
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    /** 获取 Web 上传单文件大小上限。
     * @return 上限（MB） */
    public int getMaxUploadMB() { return maxUploadMB; }
    /** 设置 Web 上传单文件大小上限。
     * @param maxUploadMB 上限（MB） */
    public void setMaxUploadMB(int maxUploadMB) { this.maxUploadMB = maxUploadMB; }
    /** 获取字符编码。
     * @return 编码名称 */
    public String getCharset() { return charset; }
    /** 设置字符编码。
     * @param charset 编码名称 */
    public void setCharset(String charset) { this.charset = charset; }
    /** 获取样式配置（嵌套对象）。
     * @return 样式配置 */
    public Style getStyle() { return style; }
    /** 设置样式配置。
     * @param style 样式配置 */
    public void setStyle(Style style) { this.style = style; }

    /**
     * 导出样式相关配置（嵌套属性 {@code ddd4j.excel.style.*}）。
     */
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

        /**
         * 显式无参构造器，供 Spring 绑定嵌套样式配置。
         */
        public Style() {
        }

        /** 是否启用默认细边框。
         * @return 启用返回 {@code true} */
        public boolean isDefaultBorder() { return defaultBorder; }
        /** 设置是否启用默认细边框。
         * @param defaultBorder 是否启用 */
        public void setDefaultBorder(boolean defaultBorder) { this.defaultBorder = defaultBorder; }
        /** 是否自动适配列宽。
         * @return 自动适配返回 {@code true} */
        public boolean isAutoSizeColumn() { return autoSizeColumn; }
        /** 设置是否自动适配列宽。
         * @param autoSizeColumn 是否自动适配 */
        public void setAutoSizeColumn(boolean autoSizeColumn) { this.autoSizeColumn = autoSizeColumn; }
        /** 获取表头行高。
         * @return 行高（1/20 磅） */
        public short getHeaderRowHeight() { return headerRowHeight; }
        /** 设置表头行高。
         * @param headerRowHeight 行高（1/20 磅） */
        public void setHeaderRowHeight(short headerRowHeight) { this.headerRowHeight = headerRowHeight; }
    }
}
