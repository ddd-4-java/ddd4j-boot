package io.ddd4j.boot.sample.infrastructure.order.persistence.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体（持久化层）
 *
 * <p>对应 t_order 表，承载订单的存储模型：状态以编码字符串落库，
 * 金额与币种分列保存，收货地址打平为省市区等独立列；
 * 创建/更新时间由 MyBatis-Plus 自动填充。领域模型与本实体的
 * 相互转换统一经 {@code OrderConverter} 完成。</p>
 */
@TableName("t_order")
public class OrderEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private String currency;
    private String province;
    private String city;
    private String district;
    private String detail;
    private String zipCode;
    private String remark;
    private LocalDateTime paidTime;
    private LocalDateTime shippedTime;
    private LocalDateTime deliveredTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 构造空订单持久化实体，仅供 MyBatis-Plus 反射映射与查询结果填充使用。
     */
    public OrderEntity() {
    }

    /** 获取主键 ID。
     * @return 主键 ID */
    public Long getId() {
        return this.id;
    }

    /** 设置主键 ID。
     * @param id 主键 ID */
    public void setId(final Long id) {
        this.id = id;
    }

    /** 获取订单编号。
     * @return 订单编号 */
    public String getOrderNo() {
        return this.orderNo;
    }

    /** 设置订单编号。
     * @param orderNo 订单编号 */
    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    /** 获取下单用户 ID。
     * @return 下单用户 ID */
    public Long getUserId() {
        return this.userId;
    }

    /** 设置下单用户 ID。
     * @param userId 下单用户 ID */
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    /** 获取状态编码。
     * @return 状态编码 */
    public String getStatus() {
        return this.status;
    }

    /** 设置状态编码。
     * @param status 状态编码 */
    public void setStatus(final String status) {
        this.status = status;
    }

    /** 获取总金额。
     * @return 总金额 */
    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    /** 设置总金额。
     * @param totalAmount 总金额 */
    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /** 获取币种。
     * @return 币种 */
    public String getCurrency() {
        return this.currency;
    }

    /** 设置币种。
     * @param currency 币种 */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** 获取省份。
     * @return 省份 */
    public String getProvince() {
        return this.province;
    }

    /** 设置省份。
     * @param province 省份 */
    public void setProvince(final String province) {
        this.province = province;
    }

    /** 获取城市。
     * @return 城市 */
    public String getCity() {
        return this.city;
    }

    /** 设置城市。
     * @param city 城市 */
    public void setCity(final String city) {
        this.city = city;
    }

    /** 获取区县。
     * @return 区县 */
    public String getDistrict() {
        return this.district;
    }

    /** 设置区县。
     * @param district 区县 */
    public void setDistrict(final String district) {
        this.district = district;
    }

    /** 获取详细地址。
     * @return 详细地址 */
    public String getDetail() {
        return this.detail;
    }

    /** 设置详细地址。
     * @param detail 详细地址 */
    public void setDetail(final String detail) {
        this.detail = detail;
    }

    /** 获取邮政编码。
     * @return 邮政编码 */
    public String getZipCode() {
        return this.zipCode;
    }

    /** 设置邮政编码。
     * @param zipCode 邮政编码 */
    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    /** 获取备注。
     * @return 备注 */
    public String getRemark() {
        return this.remark;
    }

    /** 设置备注。
     * @param remark 备注 */
    public void setRemark(final String remark) {
        this.remark = remark;
    }

    /** 获取支付时间。
     * @return 支付时间 */
    public LocalDateTime getPaidTime() {
        return this.paidTime;
    }

    /** 设置支付时间。
     * @param paidTime 支付时间 */
    public void setPaidTime(final LocalDateTime paidTime) {
        this.paidTime = paidTime;
    }

    /** 获取发货时间。
     * @return 发货时间 */
    public LocalDateTime getShippedTime() {
        return this.shippedTime;
    }

    /** 设置发货时间。
     * @param shippedTime 发货时间 */
    public void setShippedTime(final LocalDateTime shippedTime) {
        this.shippedTime = shippedTime;
    }

    /** 获取送达时间。
     * @return 送达时间 */
    public LocalDateTime getDeliveredTime() {
        return this.deliveredTime;
    }

    /** 设置送达时间。
     * @param deliveredTime 送达时间 */
    public void setDeliveredTime(final LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    /** 获取创建时间。
     * @return 创建时间 */
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    /** 设置创建时间。
     * @param createTime 创建时间 */
    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /** 获取更新时间。
     * @return 更新时间 */
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    /** 设置更新时间。
     * @param updateTime 更新时间 */
    public void setUpdateTime(final LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}