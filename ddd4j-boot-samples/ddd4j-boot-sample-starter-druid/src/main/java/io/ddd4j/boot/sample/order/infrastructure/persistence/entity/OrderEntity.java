package io.ddd4j.boot.sample.order.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ddd4j.core.ddd.model.Entity;



import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体（持久化层）
 */


@TableName("t_order")
public class OrderEntity implements Entity<Long> {
    /**
     * 构造 OrderEntity 实例。
     *
     */
    public OrderEntity() {
    }

    @Override
    public Long id() {
        return id;
    }

    /** id。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** orderNo。 */
    private String orderNo;

    /** userId。 */
    private Long userId;

    /** status。 */
    private String status;

    /** totalAmount。 */
    private BigDecimal totalAmount;

    /** currency。 */
    private String currency;

    /** province。 */
    private String province;

    /** city。 */
    private String city;

    /** district。 */
    private String district;

    /** detail。 */
    private String detail;

    /** zipCode。 */
    private String zipCode;

    /** remark。 */
    private String remark;

    /** paidTime。 */
    private LocalDateTime paidTime;

    /** shippedTime。 */
    private LocalDateTime shippedTime;

    /** deliveredTime。 */
    private LocalDateTime deliveredTime;

    /** createTime。 */
    private LocalDateTime createTime;

    /**
     * 获取Id。
     *
     * @return Id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置Id。
     *
     * @param id Id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取OrderNo。
     *
     * @return OrderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置OrderNo。
     *
     * @param orderNo OrderNo
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取UserId。
     *
     * @return UserId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置UserId。
     *
     * @param userId UserId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取Status。
     *
     * @return Status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置Status。
     *
     * @param status Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取TotalAmount。
     *
     * @return TotalAmount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置TotalAmount。
     *
     * @param totalAmount TotalAmount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取Currency。
     *
     * @return Currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置Currency。
     *
     * @param currency Currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * 获取Province。
     *
     * @return Province
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置Province。
     *
     * @param province Province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取City。
     *
     * @return City
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置City。
     *
     * @param city City
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取District。
     *
     * @return District
     */
    public String getDistrict() {
        return district;
    }

    /**
     * 设置District。
     *
     * @param district District
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 获取Detail。
     *
     * @return Detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 设置Detail。
     *
     * @param detail Detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * 获取ZipCode。
     *
     * @return ZipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * 设置ZipCode。
     *
     * @param zipCode ZipCode
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * 获取Remark。
     *
     * @return Remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置Remark。
     *
     * @param remark Remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取PaidTime。
     *
     * @return PaidTime
     */
    public LocalDateTime getPaidTime() {
        return paidTime;
    }

    /**
     * 设置PaidTime。
     *
     * @param paidTime PaidTime
     */
    public void setPaidTime(LocalDateTime paidTime) {
        this.paidTime = paidTime;
    }

    /**
     * 获取ShippedTime。
     *
     * @return ShippedTime
     */
    public LocalDateTime getShippedTime() {
        return shippedTime;
    }

    /**
     * 设置ShippedTime。
     *
     * @param shippedTime ShippedTime
     */
    public void setShippedTime(LocalDateTime shippedTime) {
        this.shippedTime = shippedTime;
    }

    /**
     * 获取DeliveredTime。
     *
     * @return DeliveredTime
     */
    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    /**
     * 设置DeliveredTime。
     *
     * @param deliveredTime DeliveredTime
     */
    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    /**
     * 获取CreateTime。
     *
     * @return CreateTime
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置CreateTime。
     *
     * @param createTime CreateTime
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
