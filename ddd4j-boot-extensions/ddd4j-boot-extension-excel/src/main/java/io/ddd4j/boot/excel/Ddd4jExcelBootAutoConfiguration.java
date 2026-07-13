package io.ddd4j.boot.excel;

import com.alibaba.excel.EasyExcel;
import io.ddd4j.boot.excel.config.ExcelProperties;
import io.ddd4j.boot.excel.web.ExcelHttpKit;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * ddd4j-boot Excel 自动配置。
 *
 * <p>装配以下 Spring Bean：
 * <ul>
 *   <li>{@link ExcelProperties}：绑定 {@code ddd4j.excel.*} 配置</li>
 *   <li>{@link ExcelHttpKit}（仅 Web 环境）：提供 {@code download(HttpServletResponse, ...)} /
 *       {@code upload(MultipartFile, ...)} 静态快捷方法</li>
 * </ul>
 *
 * <p>classpath 守卫：检测 easyexcel 的 {@link EasyExcel} 类是否存在，避免未引入依赖时启动失败。
 *
 * <p>关闭开关：{@code ddd4j.excel.enabled=false} 可禁用整个自动装配。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(EasyExcel.class)
@ConditionalOnProperty(prefix = ExcelProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ExcelProperties.class)
public class Ddd4jExcelBootAutoConfiguration {

    /**
     * Web Excel 工具 Bean（仅当 classpath 上有 {@link HttpServletResponse} 时装配）。
     *
     * <p>业务侧可直接注入使用：
     * <pre>{@code
     * @Autowired
     * private ExcelHttpKit excelHttpKit;
     *
     * @GetMapping("/download")
     * public void download(HttpServletResponse resp) {
     *     excelHttpKit.download(resp, "订单.xlsx", OrderVO.class, orderService.listAll());
     * }
     * }</pre>
     *
     * <p>注意：{@link ExcelHttpKit} 的方法都是静态的；这里的 Bean 装配主要用于让
     * "AutoConfiguration 已生效"可被 Spring 观察（业务侧不需要注入也可直接调用静态方法）。
     *
     * @param properties Excel 配置（用于上传校验等参数）
     * @return {@link ExcelHttpKit} 实例
     */
    @Bean
    @ConditionalOnMissingBean(ExcelHttpKit.class)
    @ConditionalOnClass(HttpServletResponse.class)
    public ExcelHttpKit excelHttpKit(ExcelProperties properties) {
        return new ExcelHttpKit(properties);
    }
}
