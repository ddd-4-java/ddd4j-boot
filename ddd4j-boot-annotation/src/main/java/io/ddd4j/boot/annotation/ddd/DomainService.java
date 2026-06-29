package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.stereotype.Service;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * Spring 业务服务 Bean（领域服务）
 * 
 * <p><b>核心目标</b>：业务代码只写一个 @DomainService，同时获得：
 * <ul>
 *   <li>DDD 语义（被 ArchUnit 规则识别）</li>
 *   <li>Spring 自动注册为 Bean（@Service 元注解）</li>
 *   <li>ddd4j AOP 拦截能力</li>
 * </ul>
 * 
 * <p>业务代码使用方式：
 * <pre>
 * &#64;DomainService   // ← 只需写一个注解！
 * public class UserDomainServiceImpl implements UserDomainService {
 *     // 自动被 Spring 注册为 Bean
 *     // 同时被 ArchUnit 识别为 DomainService
 * }
 * </pre>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 2.0.x
 */
@DDDAnnotation                                  // 标注为 DDD 注解（ArchUnit 识别）
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Service                                        // ★ 关键：直接用 Spring 原生元注解
@Inherited
public @interface DomainService {

    /**
     * Bean 名称（透传给 @Service）
     */
    @AliasFor(annotation = Service.class, attribute = "value")
    String value() default "";
}
