package io.ddd4j.boot.core.annotation;

import java.lang.annotation.*;

/**
 * 领域模型标记
 * @author mingjie
 * @since 2022/3/19
 * @see <a href="https://github.com/smingjie/bbq-ddd">bbq-ddd</a>
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(value = {ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
public @interface DDDAnnotation {

}