package io.ddd4j.boot.cmpt.express.infrastructure.config;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import io.ddd4j.boot.cmpt.express.infrastructure.function.ContainsFunction;
import io.ddd4j.boot.cmpt.express.infrastructure.function.EndsWithFunction;
import io.ddd4j.boot.cmpt.express.infrastructure.function.FormatDateFunction;
import io.ddd4j.boot.cmpt.express.infrastructure.function.StartsWithFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Objects;

/**
 * QLExpress配置类
 * 基础设施层：技术框架配置
 * 
 * 负责注册硬编码格式的函数（放置在 infrastructure.function 目录）
 * 以及通过注解方式声明的自定义函数
 */
@Configuration
@ConditionalOnClass(name = "com.alibaba.qlexpress4.Express4Runner")
public class QLExpressConfig {

    private static final Logger log = LoggerFactory.getLogger(QLExpressConfig.class);

    @Bean
    public Express4Runner expressRunner(ObjectProvider<CustomFunction> functionProvider) {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        try {
            // 1. 注册硬编码格式的函数（逻辑固定的函数规则）
            registerHardcodedFunctions(runner);
            
            // 2. 注册通过注解方式声明的自定义函数
            registerAnnotatedFunctions(runner, functionProvider);
            
            log.info("QLExpress函数注册完成");
        } catch (Exception e) {
            log.error("注册QLExpress函数失败", e);
            throw new RuntimeException("注册QLExpress函数失败", e);
        }

        return runner;
    }

    /**
     * 注册硬编码格式的函数
     * 这些函数逻辑固定，放置在 infrastructure.function 目录
     */
    private void registerHardcodedFunctions(Express4Runner runner) throws Exception {
        // 字符串处理函数
        runner.addFunction("contains", new ContainsFunction());
        runner.addFunction("startsWith", new StartsWithFunction());
        runner.addFunction("endsWith", new EndsWithFunction());
        
        // 日期处理函数
        runner.addFunction("formatDate", new FormatDateFunction());
        
        // 其他硬编码函数
        // 注意：HelloFunction 使用 @QLAlias 注解，会在 registerAnnotatedFunctions 中处理
    }

    /**
     * 注册通过注解方式声明的自定义函数
     * 支持 @QLAlias 注解的函数
     */
    private void registerAnnotatedFunctions(Express4Runner runner, ObjectProvider<CustomFunction> functionProvider) {
        functionProvider.forEach(function -> {
            QLAlias qlAlias = AnnotationUtils.findAnnotation(function.getClass(), QLAlias.class);
            if (Objects.nonNull(qlAlias) && qlAlias.value().length >= 2) {
                try {
                    runner.addAlias(qlAlias.value()[0], qlAlias.value()[1]);
                    log.debug("注册注解函数: {} -> {}", qlAlias.value()[0], qlAlias.value()[1]);
                } catch (Exception e) {
                    log.warn("注册注解函数失败: {}", function.getClass().getName(), e);
                }
            }
        });
    }
}

