package io.ddd4j.boot.core.annotation;

import java.lang.annotation.*;

/**
 * 领域模型标记-值对象
 */
@DDDAnnotation
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface DomainValueObject {
}