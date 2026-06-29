package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * Spring 领域转换器
 * 
 * <p>用于在领域对象与数据对象之间进行转换。
 * 
 * <p><b>核心目标</b>：业务代码只写一个 @DomainConverter，自动注册为 Spring Bean。
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface DomainConverter {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
