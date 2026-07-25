package io.ddd4j.boot.sample.order.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ddd4j.core.ddd.model.Entity;



import java.math.BigDecimal;

/**
 * 订单项实体（持久化层）
 */


@TableName("t_order_item")
public class OrderItemEntity implements Entity<Long> {

    @Override
    public Long id() {
        return id;
    }

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private String currency;
}

