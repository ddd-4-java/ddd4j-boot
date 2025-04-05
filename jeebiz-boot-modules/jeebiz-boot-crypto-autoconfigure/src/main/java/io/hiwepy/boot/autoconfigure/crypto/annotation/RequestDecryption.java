package io.hiwepy.boot.autoconfigure.crypto.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RequestDecryption {
}
