package io.ddd4j.boot.sample.exception;

import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.core.constant.Constants;
import io.ddd4j.core.CustomApiCode;
import io.ddd4j.core.exception.BizRuntimeException;
import org.springframework.extension.context.NestedMessageSource;
import org.springframework.extension.utils.SpringContextUtils;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 1、通用异常参数枚举
 * a、国际化key 是大写的字符 _ 拼接，统一使用小写的如，api.not.start
 * b、变量按服务区分，统一加上服务名， 比如 third.api.fail 就是 third 服务的，user.info.save.fail 就表示 user 服务的
 * c、编码按服务进行分段，比如
 * common：10000 - 20000，
 * user：20000+
 */
public enum BizExceptionCode implements CustomApiCode {

    // common

    /** 签名信息缺失（错误码 1403）。 */
    SIGN_MISSING("1403", 1403, "Signature information missing", "签名信息缺失"),

    /** 业务处理成功（错误码 200）。 */
    SUCCESS_V1("SUCCESS", 200, "success", "SUCCESS"),
    /** 业务处理失败（错误码 1000）。 */
    FAILED("FAILED", 1000, "failed", "失败"),
    /** 重复提交（错误码 2000）。 */
    REPEATED("REPEATED", 2000, "repeated submit", "repeated submit"),
    /** 未找到匹配记录（错误码 10000）。 */
    DATA_NOT_FOUND("data.not.found", 10000, "", "未找到记录"),
    /** 系统内部错误（错误码 9999）。 */
    SYSTEM_ERROR("system.error", 9999, "system error", "流量过大系统开小差啦，请尝试重新发起"),
    /** 系统正在升级维护（错误码 9999）。 */
    SYSTEM_UPGRADING("system.upgrading", 9999, "服务正在升级维护...，请稍后再试！", "服务正在升级维护...，请稍后再试！"),
    /** 依赖服务正在升级维护（错误码 9999）。 */
    SYSTEM_DEPEND_UPGRADING("system.depend.upgrading", 9999, "依赖服务正在升级维护...，请稍后再试！", "依赖服务正在升级维护...，请稍后再试！"),

    /** 远程服务调用失败（错误码 9998）。 */
    REMOTE_INVOKE_ERROR("remote.invoke.error", 9998, "remoteInvokeError", "远程调用失败"),
    /** 消息 key 为空（错误码 9997）。 */
    MESSAGE_KEY_IS_NULL("message.key.is.null", 9997, "messageKeyIsNull", "messageKey不能为空"),
    /** 数据库过滤条件已存在（错误码 9996）。 */
    DATABASE_FILTERS_EXISTS("database.filters.exists", 9996, "", ""),
    /** 必填参数不存在（错误码 9995）。 */
    PARAM_NOT_EXISTS("param.not.exists", 9995, "", ""),

    // 短信相关

    /** 短信发送必须指定国家或地区（错误码 1502）。 */
    SMS_SEND_REGION_REQUIRED("sms.send.region.required", 1502, "sms send region required", "请选择国家或地区"),
    /** 手机号码不合法（错误码 1502）。 */
    SMS_PHONE_ERROR("sms.phone.error", 1502, "sms send region required", "手机号码不合法"),
    /** 验证码发送次数超过上限（错误码 1504）。 */
    SMS_PHONE_MAX_ERROR("sms.phone.max.error", 1504, "phone is error", "验证码发送次数已超过上限"),
    /** 验证码发送失败（错误码 1505）。 */
    SMS_ERROR("sms.send.backlist.limit", 1505, "sms error", "验证码发送失败请重试"),
    /** 验证码校验失败（错误码 1506）。 */
    SMS_CODE_ERROR("sms.check.fail", 1506, "code is error", "验证码验证失败"),

    /** 设备已被封禁（错误码 20017）。 */
    DEVICE_BAN("login.device.ban", 20017, "Your device has been banned from logging in, please contact customer service",
            "该设备已封禁，请联系客服"),
    /** 签名过期，需要重新登录（错误码 20018）。 */
    SIGN_EXPIRED("login.token.expired", 20018, "Sign expired,Please log in again", "签名过期"),

    // 用户相关 30000-40000

    /** 保存用户信息失败（错误码 30000）。 */
    SAVE_USER_INFO_FAIL("user.info.save.fail", 30000, "保存用户信息失败", ""),
    /** 用户信息不存在（错误码 30001）。 */
    USER_INFO_IS_NOT_EXSIT("user.info.is.not.exsit", 30001, "用户信息不存在", ""),
    /** 应用用户状态异常（错误码 40001）。 */
    APP_USER_STATUS_ERROR("APP.USER.STATUS.ERROR", 40001, "应用用户状态异常", ""),
    /** 应用用户申请无权限（错误码 40002）。 */
    APP_USER_APPLY_NO_PERMISSION("app.user.apply.no.permission", 40002, "应用用户申请无权限", ""),
    /** 应用未指定归属组织机构（错误码 40002）。 */
    APP_OWNER_ORG_NOT_EXSIT("app.owner.org.no.exsit", 40002, "应用未指定归属组织机构", ""),
    /** 应用上架申请记录不存在（错误码 40002）。 */
    APP_PUBLISH_APPLY_NOT_EXSIT("app.publish.apply.no.exsit", 40002, "应用上架申请记录不存在", ""),
    /** 应用上架申请已在审核中（错误码 40002）。 */
    APP_PUBLISH_APPLY_IN_AUDITING("app.publish.apply.in.auditing", 40002, "应用上架申请已经在审核中，请勿重复操作", ""),
    /** 无应用上架审核权限（错误码 40002）。 */
    APP_PUBLISH_AUDIT_NO_PERMISSION("app.publish.audit.no.permission", 40002, "你没有该应用的上架审核权限", ""),

    /** 应用不存在（错误码 40002）。 */
    APP_IS_NOT_EXSIT("app.is.not.exsit", 40002, "应用不存在", ""),
    /** 身份异常（错误码 80002）。 */
    IDENTITY_ERROR("identity.error", 80002, "身份异常", ""),
    /** 权限异常（错误码 80003）。 */
    ROLE_ERROR("role.error", 80003, "权限异常", ""),
    /** 服务机构不匹配（错误码 40003）。 */
    APP_USER_APPLY_NO_DEPT_PERMISSION("app.user.apply.no.dept", 40003, "服务机构不匹配", ""),
    /** 服务对象不匹配（错误码 40004）。 */
    APP_USER_APPLY_NO_SCOPE_PERMISSION("app.user.apply.no.scope", 40004, "服务对象不匹配", ""),
    /** 应用使用申请已在审核中（错误码 40004）。 */
    APP_USER_APPLY_IN_AUDITING("app.user.apply.in.auditing", 40004, "应用使用申请已经在审核中，请勿重复操作", ""),
    /** 应用使用申请已审核通过，不允许再修改（错误码 40004）。 */
    APP_USER_APPLY_IS_PASS("app.user.apply.is.pass", 40004, "应用使用申请已经审核通过，不允许修改", ""),
    /** 应用状态异常（错误码 40003）。 */
    APP_STATUS_ERROR("app.status.error", 40003, "应用状态异常", "应用状态异常"),

    /** 无应用使用申请授权审核权限（错误码 40002）。 */
    APP_USER_AUDIT_NO_PERMISSION("app.user.audit.no.permission", 40002, "你没有应用使用申请授权审核权限", ""),

    /** 用户没有身份信息（错误码 40005）。 */
    APP_USER_HAS_NO_IDENTITY("app.user.has.no.identity", 40005, "此用户没有身份信息", ""),

    /** 应用未上架（错误码 40006）。 */
    APP_NOT_PUBLISH("app.not.publish", 40006, "应用未上架", "应用未上架"),
    /** 当前组织机构未上架该应用（错误码 40006）。 */
    APP_ORG_NOT_PUBLISH("app.not.publish", 40006, "当前组织机构未上架该应用", "当前组织机构未上架该应用"),

    /** 机构钉钉信息未配置（错误码 40007）。 */
    DINGDING_NOT_CONFIG("dingding.not.config", 40007, "机构钉钉信息未配置", "机构钉钉信息未配置"),
    /** 应用级别有误（错误码 40008）。 */
    APP_LEVEL_TYPE_ERROR("app.level.error", 40008, "应用级别有误", "应用级别有误"),
    /** 已申请过应用机构权限（错误码 40009）。 */
    APP_ORG_APPLY_ALREADY("app.org.apply.already", 40009, "已申请应用机构权限", "已申请应用机构权限"),
    /** 已通过应用机构权限（错误码 40010）。 */
    APP_ORG_PASS_ALREADY("app.org.pass.already", 40010, "已通过应用机构权限", "已通过应用机构权限"),
    /** 应用 clientId 已存在（错误码 40011）。 */
    APP_CLIENTID_EXSIT("app.clientId.exsit", 40011, "应用clientId已经存在，请关闭页面重新尝试", ""),
    /** 应用归属机构不可为空（错误码 40012）。 */
    APP_OWNER_ORG_REQUIRED("app.owner.org.required", 40012, "应用归属机构不可为空", "应用归属机构不可为空"),


    /** 未找到应用申请记录（错误码 50001）。 */
    APP_TASK_NOT_EXSIT("app.task.not.exsit", 50001, "未找到应用申请记录", "未找到应用申请记录"),
    /** 应用清单状态异常（错误码 50002）。 */
    APP_TASK_STATUS_ERROR("app.task.status.error", 50002, "清单状态异常", "清单状态异常"),
    /** 已存在提交过的清单（错误码 50003）。 */
    APP_TASK_STATUS_APPLY_EXIST("app_task.status.apply.exist", 50003, "存在已提交清单", "存在已提交清单"),
    /** 数据仓连接失败（错误码 50005）。 */
    APP_TASK_DBUS_ERROR("app.task.dbus.error", 50005, "数据仓连接失败", "数据仓连接失败"),
    /** 清单明细为空（错误码 50006）。 */
    APP_TASK_INFO_EMPT("app.task.info.empt", 50006, "清单明细空", "清单明细空"),

    /** 钉钉分组不存在（错误码 50001）。 */
    DINGDING_APP_GROUP_DICT_NOT_EXIST("dingding.app.group.dict.not.exist", 50001, "钉钉分组不存在", "钉钉分组不存在"),
    /** 钉钉分组已存在（错误码 50002）。 */
    DINGDING_APP_GROUP_DICT_NAME_EXIST("dingding.app.group.dict.name.exist", 50002, "钉钉分组已存在", "钉钉分组已存在"),
    ;

    /**
     * 国际化Key
     */
    private String i18nKey;

    /**
     * 错误编号
     */
    private int errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 错误描述
     */
    private String desc;


    private BizExceptionCode(String i18nKey, int errorCode, String errorMsg, String desc) {
        this.i18nKey = i18nKey;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.desc = desc;
    }

    /**
     * 按错误编号抛出对应的业务运行时异常。
     * <p>
     * 编号未匹配到任何枚举（或入参为空）时，回退为系统错误枚举再抛出。
     * </p>
     *
     * @param errorCode 待匹配的错误编号
     * @throws BizRuntimeException 未匹配到编号时抛出系统错误，否则抛出匹配到的业务异常
     */
    public static void throwByErrorcode(Integer errorCode) {
        getByErrorcode(errorCode).throwException();
    }

    /**
     * 按接口返回对象的状态码与消息直接抛出业务运行时异常。
     *
     * @param response 统一接口返回对象
     * @throws BizRuntimeException 由返回对象的编码与消息构建的业务异常
     */
    public static void throwByResponse(ApiRestResponse<?> response) {
        throw new BizRuntimeException(response.getCode(), response.getMessage());
    }

    /**
     * 按错误编号查找匹配的异常枚举。
     *
     * @param errorCode 待匹配的错误编号
     * @return 匹配到的枚举实例；编号为空或未匹配时返回 {@link BizExceptionCode#SYSTEM_ERROR}
     */
    public static BizExceptionCode getByErrorcode(Integer errorCode) {
        if (errorCode == null) {
            return BizExceptionCode.SYSTEM_ERROR;
        }
        BizExceptionCode[] errorCodes = values();

        for (BizExceptionCode bizErrorCode : errorCodes) {
            if (errorCode.equals(bizErrorCode.getErrorCode())) {
                return bizErrorCode;
            }
        }
        return BizExceptionCode.SYSTEM_ERROR;
    }

    /**
     * 以当前枚举为内容直接抛出业务运行时异常。
     *
     * @throws BizRuntimeException 由当前枚举构建的业务异常
     */
    public void throwException() {
        throw this.asException();
    }

    /**
     * 将当前枚举转换为业务运行时异常对象（不抛出）。
     *
     * @return 携带错误编号、国际化 Key 与错误信息的业务异常
     */
    public BizRuntimeException asException() {
        return new BizRuntimeException(this.getErrorCode(), this.getI18nKey(), this.getErrorMsg());
    }

    /**
     * 1、根据异常枚举构建接口返回对象
     *
     * @param <T> 接口返回对象承载的数据类型
     * @param args 额外参数
     * @return 接口返回对象
     */
    public <T> ApiRestResponse<T> asResponse(Object... args) {
        NestedMessageSource messageSource = SpringContextUtils.getContext().getApplicationContext().getBean(NestedMessageSource.class);
        return this.asResponse(messageSource, args);
    }

    /**
     * 1、根据异常枚举构建接口返回对象
     *
     * @param <T> 接口返回对象承载的数据类型
     * @param messageSource I18N 国际化资源对象
     * @param args          额外参数
     * @return 接口返回对象
     */
    public <T> ApiRestResponse<T> asResponse(NestedMessageSource messageSource, Object... args) {
        this.errorMsg = messageSource.getMessage(this.getI18nKey(), args, this.getErrorMsg(), LocaleContextHolder.getLocale());
        return ApiRestResponse.of(this);
    }

    /**
     * 返回国际化消息 Key。
     *
     * @return 国际化消息 Key
     */
    public String getI18nKey() {
        return i18nKey;
    }

    /**
     * 返回错误描述文本。
     *
     * @return 错误描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 返回错误编号。
     *
     * @return 错误编号
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * 返回错误信息文本。
     *
     * @return 错误信息
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 返回统一响应使用的错误编码。
     *
     * @return 错误编号
     */
    @Override
    public int getCode() {
        return errorCode;
    }

    /**
     * 返回统一响应使用的失败原因。
     *
     * @return 错误信息
     */
    @Override
    public String getReason() {
        return errorMsg;
    }

    /**
     * 返回统一响应使用的状态标识。
     *
     * @return 恒为失败状态标识 {@link Constants#RT_FAIL}
     */
    @Override
    public String getStatus() {
        return Constants.RT_FAIL;
    }

}
