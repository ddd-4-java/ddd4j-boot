package io.hiwepy.boot.autoconfigure.satoken;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(chain = true)
@Data
public class SaTempToken implements Serializable {

    /**
     * 认证方式
     * - password，密码登录：用户输入账号和密码
     * - qrcode，扫码登录：用户扫描二维码登录
     * - sms，短信登录：用户输入手机号和验证码登录
     * - face，人脸登录：用户人脸识别登录
     * - dingtalk-ma，钉钉小程序登录：用户在钉钉小程序内登录
     * - dingtalk-scancode，钉钉扫码登录：用户使用钉钉扫码登录
     * - dingtalk-tmpcode，钉钉临时码登录：用户在钉钉客户端/App内免密登录
     * - wx-ma，微信小程序登录：用户在微信小程序内登录
     * - wx-mp，微信公众平台登录：用户在微信公众号内登录
     * - zheliban，浙里办登录：用户在浙里办内登录，如：学在浙江、学在xx系列
     * - app，应用免登：通过appKey和appSecret进行免登
     * - cas，Cas登录：通过标准的JWT登录
     * - jwt，JWT登录：通过标准的JWT登录
     * - pac4j，三方登录：基于Pac4j进行各类三方登录
     */
    private String authType;

    //==============================认证授权信息====================================

    /**
     * 用户 OpenId
     * 说明：openid 是用户在某一 client 下的唯一标识，其有如下特点：
     * - 一个用户在同一个 client 下，openid 是固定的，每次请求都会返回相同的值。
     * - 一个用户在不同的 client 下，openid 是不同的，会返回不同的值。
     */
    private String openid;
    /**
     * 用户 UnionId
     * 说明：UnionId 的特点与 OpenId 几乎一致：同一用户在不同 client 里的 UnionId 值是不同的，除非这些应用属于同一主体。
     * 例如：甲公司申请了应用A、应用B、应用C，乙公司申请了应用D、应用F，那么用户张三：
     * - 在应用 A、B、C 里的 UnionId 值一致。
     * - 在应用 D、F 里的 UnionId 值一致。
     * - 在应用 A 和 应用 D 之间，UnionId 值不一致。
     */
    private String unionId;
    /**
     * 用户 loginId（一般是账号Id）
     */
    private String loginId;

    //==============================辅助信息====================================

    /**
     * 用户最新经度（可选）
     */
    private double longitude;
    /**
     * 用户最新纬度（可选）
     */
    private double latitude;

    //==============================此次登录的请求来源====================================

    /**
     * 此次登录的客户端ID
     */
    private String appId;
    /**
     * 此次登录的客户端渠道编码
     */
    private String appChannel;
    /**
     * 此次登录的客户端版本号
     */
    private String appVersion;
    /**
     * 此次登录的请求来源IP地址
     */
    private String ipAddress;
    /**
     * 此次登录的客户端设备类型
     */
    private String deviceType;
    /**
     * 此次登录的客户端设备id
     */
    private String deviceId;

}
