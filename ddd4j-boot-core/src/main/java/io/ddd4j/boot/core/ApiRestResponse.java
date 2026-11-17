/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * model for interacting with client.
 */
@ApiModel(value = "R", description = "接口响应对象")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class R<T> {

    @ApiModelProperty(name = "code", dataType = "Integer", value = "成功或异常编码")
    @Getter
    private final int code;

    @ApiModelProperty(name = "status", dataType = "String", value = "旧接口成功、失败或异常辅助判断标记:success、fail、error", allowableValues = "success,fail,error")
    @Getter
    private final String status;

    @ApiModelProperty(name = "message", dataType = "String", value = "成功或异常消息")
    @Getter
    private final String message;

    @ApiModelProperty(name = "data", dataType = "java.lang.Object", value = "成功或异常数据")
    @Getter
    private T data;

    @ApiModelProperty(name = "error", dataType = "java.util.List<Map<String, String>>", value = "校验失败信息")
    private List<Map<String, String>> error;

    public R() {
        this.code = ApiCode.SUCCESS.getCode();
        this.status = Constants.RT_SUCCESS;
        this.message = ApiCode.SUCCESS.getReason();
    }

    protected R(final ApiCode code) {
        this.code = code.getCode();
        ;
        this.status = code.getStatus();
        this.message = code.getReason();
    }

    protected R(final ApiCode code, final T data) {
        this.code = code.getCode();
        ;
        this.status = code.getStatus();
        this.message = code.getReason();
        this.data = data;
    }

    protected R(final CustomApiCode code) {
        this.code = code.getCode();
        ;
        this.status = code.getStatus();
        this.message = code.getReason();
    }

    protected R(final CustomApiCode code, final T data) {
        this.code = code.getCode();
        ;
        this.status = code.getStatus();
        this.message = code.getReason();
        this.data = data;
    }

    protected R(final ApiCode code, final String message, final T data) {
        this.code = code.getCode();
        ;
        this.status = code.getStatus();
        this.message = message;
        this.data = data;
    }

    protected R(final ApiCode code, final String message, final T data, List<Map<String, String>> error) {
        this.code = code.getCode();
        ;
        this.status = code.getStatus();
        this.message = message;
        this.data = data;
        this.error = error;
    }

    protected R(final int code, final String message) {
        this(code, Constants.RT_SUCCESS, message);
    }

    protected R(final int code, final String status, final String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    protected R(final int code, final String message, final T data) {
        this(code, Constants.RT_SUCCESS, message, data);
    }

    protected R(final int code, final String status, final String message, final T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // success -----------------------------------------------------------------

    public static <T> R<T> success(final String message) {
        return of(ApiCode.SUCCESS, message, null);
    }

    public static <T> R<T> success(final T data) {
        return of(ApiCode.SUCCESS, data);
    }

    public static <T> R<T> success(final ApiCode code, final T data) {
        return of(code, data);
    }

    public static <T> R<T> success(final int code, final String message) {
        return of(code, Constants.RT_SUCCESS, message);
    }

    public static <T> R<T> success(final ApiCode code, final String message) {
        return of(code, message, null);
    }

    // fail -----------------------------------------------------------------

    public static <T> R<T> fail(final String message) {
        return of(ApiCode.FAIL, message, null);
    }

    public static <T> R<T> fail(final T data) {
        return of(ApiCode.FAIL, data);
    }

    public static <T> R<T> fail(final ApiCode code, final T data) {
        return of(code, data);
    }

    public static <T> R<T> fail(final int code, final String message) {
        return of(code, Constants.RT_FAIL, message);
    }

    public static <T> R<T> fail(final ApiCode code, final String message) {
        return of(code, message, null);
    }

    // error -----------------------------------------------------------------

    public static <T> R<T> error(final String message) {
        return of(ApiCode.SERVER_ERROR, message, null);
    }

    public static <T> R<T> error(final T data) {
        return of(ApiCode.SERVER_ERROR, data);
    }

    public static <T> R<T> error(final ApiCode code, final T data) {
        return of(code, data);
    }

    public static <T> R<T> error(final int code, final String message) {
        return of(code, Constants.RT_ERROR, message);
    }

    public static <T> R<T> error(final ApiCode code, final String message) {
        return of(code, message, null);
    }

    public static <T> R<T> error(final ApiCode code, final String message, List<Map<String, String>> error) {
        return of(code, message, null, error);
    }

    // -----------------------------------------------------------------

    public static <T> R<T> of(final ApiCode code) {
        return new R<T>(code);
    }

    public static <T> R<T> of(final ApiCode code, final T data) {
        return new R<T>(code, data);
    }

    public static <T> R<T> of(final ApiCode code, final String message, final T data) {
        return new R<T>(code, message, data);
    }

    public static <T> R<T> of(final ApiCode code, final String message, final T data, List<Map<String, String>> error) {
        return new R<T>(code, message, data, error);
    }

    public static <T> R<T> of(final String code, final String message) {
        return new R<T>(Integer.parseInt(code), message);
    }

    public static <T> R<T> of(final int code, final String message) {
        return new R<T>(code, message);
    }

    public static <T> R<T> of(final String code, final String status, final String message) {
        return of(Integer.parseInt(code), status, message, null);
    }

    public static <T> R<T> of(final int code, final String status, final String message) {
        return of(code, status, message, null);
    }

    public static <T> R<T> of(final int code, final String status, final String message, final T data) {
        return new R<T>(code, status, message, data);
    }

    public static <T> R<T> of(final CustomApiCode code) {
        return new R<T>(code);
    }

    public static <T> R<T> of(final CustomApiCode code, final T data) {
        return new R<T>(code, data);
    }

    @JsonIgnore
    public List<Map<String, String>> getError() {
        return error;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return status == Constants.RT_SUCCESS || code == ApiCodeValue.SC_SUCCESS;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> rtMap = new HashMap<String, Object>();
        rtMap.put("code", code);
        rtMap.put("status", status);
        rtMap.put("message", message);
        rtMap.put("data", data);
        return rtMap;
    }

}
