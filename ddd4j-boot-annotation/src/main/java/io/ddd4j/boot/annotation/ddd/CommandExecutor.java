package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * Spring 命令执行器（CQRS 写侧）
 * 
 * <p><b>核心目标</b>：业务代码只写一个 @CommandExecutor，同时获得：
 * <ul>
 *   <li>DDD 语义（被 ArchUnit 规则识别）</li>
 *   <li>Spring 自动注册为 Bean（@Component 元注解）</li>
 * </ul>
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface CommandExecutor {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
