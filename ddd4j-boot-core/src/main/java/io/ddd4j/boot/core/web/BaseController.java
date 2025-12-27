/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.core.web;

import io.ddd4j.boot.core.ApiRestResponse;
import io.ddd4j.boot.core.exception.PayloadExceptionEvent;
import io.ddd4j.boot.core.util.HttpStatus;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.context.NestedMessageSource;
import org.springframework.context.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringValueResolver;

@ApiResponses({
    @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "参数类型不匹配或格式不正确", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = "不允许访问（功能未授权）", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_FORBIDDEN, message = "服务器拒绝请求", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "请求地址不存在", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_METHOD_NOT_ALLOWED, message = "不支持的请求方法", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_NOT_ACCEPTABLE, message = "不匹配的媒体类型", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "不支持的媒体类型", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_REQUEST_TOO_LONG, message = "请求实体过大", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "服务器内部错误", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_BAD_GATEWAY, message = "错误网关", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_SERVICE_UNAVAILABLE, message = "服务不可用", response = ApiRestResponse.class),
    @ApiResponse(code = HttpStatus.SC_GATEWAY_TIMEOUT, message = "网关访问超时", response = ApiRestResponse.class)
})
public class BaseController implements ApplicationEventPublisherAware, ApplicationContextAware, EmbeddedValueResolverAware {

    @Getter
    private StringValueResolver valueResolver;
    @Getter
    private ApplicationEventPublisher eventPublisher;
    @Getter
    private ApplicationContext context;
    @Autowired(required = false)
    @Getter
    private NestedMessageSource messageSource;

    /**
     * 统一处理异常，并抛出异常事件方便进行统一的日志实现
     */
    protected void logException(Object source, Exception ex) {
        getEventPublisher().publishEvent(new PayloadExceptionEvent(source, ex));
    }

    /**
     * 获取国际化信息
     *
     * @param key  国际化Key
     * @param args 参数
     * @return 国际化字符串
     */
    protected String getMessage(String key, Object... args) {
        return getMessageSource().getMessage(key, args, LocaleContextHolder.getLocale());
    }

    protected <T> ApiRestResponse<T> success(String key, Object... args) {
        return ApiRestResponse.success(getMessage(key, args));
    }

    protected <T> ApiRestResponse<T> fail(String key, Object... args) {
        return ApiRestResponse.fail(getMessage(key, args));
    }

    protected <T> ApiRestResponse<T> error(String key, Object... args) {
        return ApiRestResponse.error(getMessage(key, args));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.valueResolver = resolver;
    }

}
