package io.ddd4j.boot.core.annotation;

import java.lang.annotation.*;

/**
 * 领域模型标记-事件
 */
@DDDAnnotation
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(value = {ElementType.TYPE,ElementType.FIELD})
public @interface DomainEvent {
}