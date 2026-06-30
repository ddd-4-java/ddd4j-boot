package io.ddd4j.boot.excel;

import io.ddd4j.extension.excel.ExcelAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot Excel 自动配置。
 *
 * <p>通过 {@link Import} 导入库侧 {@link ExcelAutoConfiguration}，
 * 提供 EasyExcel 的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(ExcelAutoConfiguration.class)
@Import(ExcelAutoConfiguration.class)
public class Ddd4jExcelBootAutoConfiguration {

}
