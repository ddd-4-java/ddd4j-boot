/**
 * Copyright (C) 2018 redacted-legacy-family (http://redacted-legacy-family.io).
 * All Rights Reserved.
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
package io.ddd4j.boot.data.mybatis.web;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import io.ddd4j.core.ApiCode;
import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.web.webmvc.exception.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.plugin.PluginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extension.context.NestedMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常增强，以JSON的形式返回给客服端
 * 异常增强类型：NullPointerException,RunTimeException,ClassCastException,
 * NoSuchMethodException,IOException,IndexOutOfBoundsException
 *
 * <p>本类补充 MyBatis / MyBatis-Plus 持久层异常到 HTTP 500 统一响应体的映射，
 * 由 {@code @ControllerAdvice} 全局生效。
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class MybatisExceptionHandler extends BaseExceptionHandler {

    @Autowired
    private NestedMessageSource messageSource;

    /**
     * 显式无参构造器，供 Spring 实例化该 ControllerAdvice。
     */
    public MybatisExceptionHandler() {
    }

    /**---------------------Mybatis 异常----------------------------*/

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis 绑定异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({BindingException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisBindingException(BindingException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis:绑定异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis 缓存异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({CacheException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisCacheException(CacheException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis:缓存异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis 数据源异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({DataSourceException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisDataSourceException(DataSourceException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis:数据源异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis 插件异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({PluginException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisPluginException(PluginException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis:插件异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis 结果映射异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({ResultMapException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisResultMapException(ResultMapException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis:结果集异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis 查询返回多条结果异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({TooManyResultsException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisTooManyResultsException(TooManyResultsException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis:结果集异常,返回了多条数据");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis 持久化内部异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({PersistenceException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisPersistenceException(PersistenceException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis 内部异常：" + ex.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     *
     * @param ex MyBatis-Plus 框架异常
     * @return 统一错误响应体（HTTP 500）
     */
    @ExceptionHandler({MybatisPlusException.class})
    public ResponseEntity<ApiRestResponse<String>> mybatisPlusException(MybatisPlusException ex) {
        this.logException(ex);
        ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("MyBatis Plus 异常：" + ex.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 获取国际化消息源。
     *
     * @return 嵌套消息源实例
     */
    public NestedMessageSource getMessageSource() {
        return messageSource;
    }

}
