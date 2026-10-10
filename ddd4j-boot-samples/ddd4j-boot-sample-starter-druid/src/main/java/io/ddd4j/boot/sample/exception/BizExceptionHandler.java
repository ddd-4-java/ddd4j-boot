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
 * BizExceptionHandler 类
 */
@ControllerAdvice
public class BizExceptionHandler extends BaseExceptionHandler {
    /**
     * 构造 BizExceptionHandler 实例。
     *
     */
    public BizExceptionHandler() {
    }

    /** messageSource。 */
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
