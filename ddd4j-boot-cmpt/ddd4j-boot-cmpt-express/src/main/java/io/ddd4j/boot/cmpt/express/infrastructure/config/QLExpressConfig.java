package io.ddd4j.boot.cmpt.express.infrastructure.config;

import io.ddd4j.boot.cmpt.express.infrastructure.function.ContainsFunction;
import io.ddd4j.boot.cmpt.express.infrastructure.function.EndsWithFunction;
import io.ddd4j.boot.cmpt.express.infrastructure.function.FormatDateFunction;
import io.ddd4j.boot.cmpt.express.infrastructure.function.StartsWithFunction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 使用反射或条件注解来避免直接依赖QLExpress类
// 实际使用时需要确保qlexpress4依赖已添加

/**
 * QLExpress配置类
 * 基础设施层：技术框架配置
 * 
 * 注意：由于QLExpress的包名可能因版本而异，这里使用反射来创建实例
 * 实际使用时需要根据qlexpress4的具体版本来调整包名
 */
@Configuration
@ConditionalOnClass(name = "com.ql.util.express.ExpressRunner")
public class QLExpressConfig {

    @Bean
    public Object expressRunner() {
        try {
            // 使用反射创建ExpressRunner，避免编译时依赖
            Class<?> runnerClass = Class.forName("com.ql.util.express.ExpressRunner");
            Object runner = runnerClass.getDeclaredConstructor().newInstance();
            
            // 注册自定义函数
            java.lang.reflect.Method addFunctionMethod = runnerClass.getMethod("addFunction", String.class, Object.class);
            addFunctionMethod.invoke(runner, "contains", new ContainsFunction("contains"));
            addFunctionMethod.invoke(runner, "startsWith", new StartsWithFunction("startsWith"));
            addFunctionMethod.invoke(runner, "endsWith", new EndsWithFunction("endsWith"));
            addFunctionMethod.invoke(runner, "formatDate", new FormatDateFunction("formatDate"));
            
            return runner;
        } catch (Exception e) {
            throw new RuntimeException("初始化QLExpress失败，请确保qlexpress4依赖已添加", e);
        }
    }

    @Bean
    public Object expressContext() {
        try {
            Class<?> contextClass = Class.forName("com.ql.util.express.DefaultContext");
            return contextClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建QLExpress上下文失败", e);
        }
    }
}

