/**
 * Copyright (C) 2018 ddd4j (https://github.com/easy4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.exception;

import io.ddd4j.web.webmvc.exception.BaseExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extension.context.NestedMessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 业务异常全局处理器，继承基础异常处理器并提供国际化消息源。
 */
@ControllerAdvice
public class BizExceptionHandler extends BaseExceptionHandler {

    /**
     * 构造业务异常处理器实例。
     */
    public BizExceptionHandler() {
    }

    /**
     * I18N 国际化消息源。
     */
    @Autowired
    protected NestedMessageSource messageSource;

    /**
     * 500 (降级熔断)
     @ExceptionHandler({ ClientException.class })
     @ResponseBody public ResponseEntity<ApiRestResponse<String>> netflixClientException(ClientException ex) {
     this.logException(ex);
     return new ResponseEntity<>( BizExceptionCode.SYSTEM_DEPEND_UPGRADING.asResponse(messageSource), HttpStatus.OK);
     }
     */

}
