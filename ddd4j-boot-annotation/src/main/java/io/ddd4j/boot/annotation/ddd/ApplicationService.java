package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Spring 应用层服务
 *
 * <p><b>核心目标</b>：业务代码只写一个 @ApplicationService，同时获得：
 * <ul>
 *   <li>DDD 语义（被 ArchUnit 规则识别）</li>
 *   <li>Spring 自动注册为 Bean（@Service 元注解）</li>
 * </ul>
 *
 * <p>应用层服务编排领域服务、事务管理、事件发布。
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Service
@Inherited
public @interface ApplicationService {

    @AliasFor(annotation = Service.class, attribute = "value")
    String value() default "";
}
