/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.exception;

import io.ddd4j.web.webmvc.exception.BaseExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extension.context.NestedMessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 业务异常统一处理组件。
 * <p>
 * 通过 {@code @ControllerAdvice} 跨控制器收集业务异常，
 * 复用 {@code BaseExceptionHandler} 的异常映射逻辑，
 * 并注入 {@code NestedMessageSource} 用于异常消息的国际化解析。
 * </p>
 */
@ControllerAdvice
public class BizExceptionHandler extends BaseExceptionHandler {

    /**
     * 国际化消息源，由容器注入，供异常消息解析使用。
     */
    @Autowired
    protected NestedMessageSource messageSource;

    /**
     * 构造业务异常统一处理组件（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public BizExceptionHandler() {
    }

    /**
     * 500 (降级熔断)
     @ExceptionHandler({ ClientException.class })
     @ResponseBody public ResponseEntity<ApiRestResponse<String>> netflixClientException(ClientException ex) {
     this.logException(ex);
     return new ResponseEntity<>( BizExceptionCode.SYSTEM_DEPEND_UPGRADING.asResponse(messageSource), HttpStatus.OK);
     }
     */

}
