/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
package io.ddd4j.boot.data.mybatis.web;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import io.ddd4j.core.api.ApiCode;
import io.ddd4j.core.api.R;
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
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class MybatisExceptionHandler extends BaseExceptionHandler {

    @Autowired
    private NestedMessageSource messageSource;

    /**---------------------Mybatis 异常----------------------------*/

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({BindingException.class})
    public ResponseEntity<R<String>> mybatisBindingException(BindingException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis:绑定异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({CacheException.class})
    public ResponseEntity<R<String>> mybatisCacheException(CacheException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis:缓存异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({DataSourceException.class})
    public ResponseEntity<R<String>> mybatisDataSourceException(DataSourceException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis:数据源异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({PluginException.class})
    public ResponseEntity<R<String>> mybatisPluginException(PluginException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis:插件异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({ResultMapException.class})
    public ResponseEntity<R<String>> mybatisResultMapException(ResultMapException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis:结果集异常");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({TooManyResultsException.class})
    public ResponseEntity<R<String>> mybatisTooManyResultsException(TooManyResultsException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis:结果集异常,返回了多条数据");
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({PersistenceException.class})
    public ResponseEntity<R<String>> mybatisPersistenceException(PersistenceException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis 内部异常：" + ex.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 (Internal Server Error)
     */
    @ExceptionHandler({MybatisPlusException.class})
    public ResponseEntity<R<String>> mybatisPlusException(MybatisPlusException ex) {
        this.logException(ex);
        R<String> resp = ApiCode.SERVER_ERROR.toResponse("MyBatis Plus 异常：" + ex.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public NestedMessageSource getMessageSource() {
        return messageSource;
    }

}
