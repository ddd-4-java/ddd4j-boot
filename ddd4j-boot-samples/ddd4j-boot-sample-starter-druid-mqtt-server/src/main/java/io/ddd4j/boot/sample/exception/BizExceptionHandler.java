/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class BizExceptionHandler {

    /**
     * 500 (降级熔断)
     @ExceptionHandler({ ClientException.class })
     @ResponseBody public ResponseEntity<ApiRestResponse<String>> netflixClientException(ClientException ex) {
     this.logException(ex);
     return new ResponseEntity<>( BizExceptionCode.SYSTEM_DEPEND_UPGRADING.asResponse(messageSource), HttpStatus.OK);
     }
     */

}
