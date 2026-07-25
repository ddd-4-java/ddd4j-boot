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

    @Override
    public Long id() {
        return id;
    }

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
}

