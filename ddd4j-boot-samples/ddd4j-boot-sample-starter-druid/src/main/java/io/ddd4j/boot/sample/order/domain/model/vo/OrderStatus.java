package io.ddd4j.boot.sample.order.domain.model.vo;

import lombok.Getter;

/**
 * 订单状态值对象
 */
@Getter
public enum OrderStatus {
    
    PENDING("PENDING", "待支付"),
    PAID("PAID", "已支付"),
    SHIPPED("SHIPPED", "已发货"),
    DELIVERED("DELIVERED", "已送达"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");
    
    private final String code;
    private final String description;
    
    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public boolean canTransitionTo(OrderStatus target) {
        switch (this) {
            case PENDING:
                return target == PAID || target == CANCELLED;
            case PAID:
                return target == SHIPPED || target == CANCELLED;
            case SHIPPED:
                return target == DELIVERED;
            case DELIVERED:
                return target == COMPLETED;
            case COMPLETED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }
}

