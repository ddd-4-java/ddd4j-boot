/**
 * Copyright (C) 2018 redacted-legacy-family (http://redacted-legacy-family.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.exception;

import io.ddd4j.web.webmvc.exception.BaseExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 业务异常统一处理器。
 *
 * <p>通过 {@code @ControllerAdvice} 拦截控制器抛出的业务异常，
 * 交由父类统一转换为规范的接口响应。
 */
@ControllerAdvice
public class BizExceptionHandler extends BaseExceptionHandler {

    /**
     * 构造业务异常统一处理器。
     *
     */
    public BizExceptionHandler() {
    }

}