package io.ddd4j.boot.cmpt.express.infrastructure.config;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QLExpress配置类
 * 基础设施层：技术框架配置
 */
@Configuration
@ConditionalOnClass(name = "com.alibaba.qlexpress4.Express4Runner")
public class QLExpressConfig {

    @Bean
    public Express4Runner expressRunner() {
        // 使用builder模式创建InitOptions
        InitOptions initOptions = InitOptions.builder().build();
        return new Express4Runner(initOptions);
    }
}

