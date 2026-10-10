/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.exception;

import io.ddd4j.web.webmvc.exception.BaseExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extension.context.NestedMessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 业务异常全局处理示例：以 {@code @ControllerAdvice} 继承 {@code BaseExceptionHandler} 统一承接业务异常。
 * <p>样例中具体的 {@code @ExceptionHandler} 方法暂以注释块保留，示意降级熔断返回形态。</p>
 *
 * @author ddd4j
 * @since 4.0.x
 */
@ControllerAdvice
public class BizExceptionHandler extends BaseExceptionHandler {

    /**
     * 嵌套国际化消息源，供异常响应体组装文案使用。
     */
    @Autowired
    protected NestedMessageSource messageSource;

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
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
