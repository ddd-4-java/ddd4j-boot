package io.ddd4j.boot.express;

import io.ddd4j.extension.express.infrastructure.config.ExpressAutoConfiguration;
import io.ddd4j.extension.express.infrastructure.config.QLExpressConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j qlexpress 规则引擎的 Spring Boot 整合入口。
 *
 * <p>仅负责 Boot 层的自动装配：当 classpath 上存在
 * {@code com.alibaba.qlexpress4.Express4Runner} 时，导入库
 * {@code ddd4j-extension-qlexpress} 提供的基础设施配置类，完成
 * QLExpress 运行器、规则缓存、规则引擎等领域/应用 Bean 的注册。
 *
 * <p>规则引擎的领域模型、应用服务、基础设施实现等均由
 * {@code io.ddd4j:ddd4j-extension-qlexpress} 提供，本模块不重复实现。
 *
 * @author ddd4j-boot
 * @since 1.0
 */
@AutoConfiguration
@ConditionalOnClass(name = "com.alibaba.qlexpress4.Express4Runner")
@Import({QLExpressConfig.class, ExpressAutoConfiguration.class})
public class Ddd4jExpressBootAutoConfiguration {
}
