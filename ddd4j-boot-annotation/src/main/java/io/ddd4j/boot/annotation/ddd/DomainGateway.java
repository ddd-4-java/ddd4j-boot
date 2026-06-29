package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * Spring 领域网关（防腐层）
 * 
 * <p>用于标注外部服务调用的网关接口（ACL）。
 * 
 * <p><b>核心目标</b>：业务代码只写一个 @DomainGateway，自动注册为 Spring Bean。
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface DomainGateway {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
