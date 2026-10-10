package io.ddd4j.boot.sample.order.domain.model.vo;


/**
 * 订单状态值对象
 */

public enum OrderStatus {

    /** 待支付（PENDING）。 */
    PENDING("PENDING", "待支付"),
    /** 已支付（PAID）。 */
    PAID("PAID", "已支付"),
    /** 已发货（SHIPPED）。 */
    SHIPPED("SHIPPED", "已发货"),
    /** 已送达（DELIVERED）。 */
    DELIVERED("DELIVERED", "已送达"),
    /** 已完成（COMPLETED）。 */
    COMPLETED("COMPLETED", "已完成"),
    /** 已取消（CANCELLED）。 */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取Code。
     *
     * @return Code
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取Description。
     *
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 执行 canTransitionTo 操作。
     *
     * @param target 目标状态
     *
     * @return 校验结果
     */
    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case PENDING -> target == PAID || target == CANCELLED;
            case PAID -> target == SHIPPED || target == CANCELLED;
            case SHIPPED -> target == DELIVERED;
            case DELIVERED -> target == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}

