package io.ddd4j.boot.core.annotation;

import java.lang.annotation.*;

/**
 * 领域模型标记-实体
 */
@DDDAnnotation
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(value = {ElementType.TYPE, ElementType.FIELD})
public @interface DomainEntity {

    /**
     * 是否是聚合根
     */
    boolean aggregateRoot() default false;

}