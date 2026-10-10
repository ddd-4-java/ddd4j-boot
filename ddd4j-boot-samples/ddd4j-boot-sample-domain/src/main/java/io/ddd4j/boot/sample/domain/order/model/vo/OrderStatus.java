package io.ddd4j.boot.sample.domain.order.model.vo;

/**
 * 订单状态值对象
 *
 * <p>领域语义：订单聚合的核心状态机，编码（code）用于对外协议与持久化，
 * 描述（description）用于展示；状态之间的合法流转由
 * {@link #canTransitionTo(OrderStatus)} 统一裁决，非法流转一律拒绝，
 * 保证订单生命周期不可逆、不可跳跃（如已取消不可再回到待支付）。</p>
 */
public enum OrderStatus {

    /** 待支付：订单刚创建，允许支付或取消。 */
    PENDING("PENDING", "待支付"),
    /** 已支付：等待商家发货，允许发货或取消（退款场景）。 */
    PAID("PAID", "已支付"),
    /** 已发货：物流运输中，等待送达。 */
    SHIPPED("SHIPPED", "已发货"),
    /** 已送达：等待买家确认收货。 */
    DELIVERED("DELIVERED", "已送达"),
    /** 已完成：终态，订单流程结束。 */
    COMPLETED("COMPLETED", "已完成"),
    /** 已取消：终态，订单被主动或超时取消。 */
    CANCELLED("CANCELLED", "已取消");

    /** 状态编码，与 {@code code} 构造参数一致，用于持久化与接口报文。 */
    private final String code;
    /** 状态中文描述，用于界面展示。 */
    private final String description;

    /**
     * 构造订单状态。
     *
     * @param code        状态编码
     * @param description 状态中文描述
     */
    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取状态编码。
     *
     * @return 状态编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态中文描述。
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 判断当前状态是否允许流转到目标状态（状态机迁移规则）。
     *
     * <p>流转规则：待支付 → 已支付/已取消；已支付 → 已发货/已取消；
     * 已发货 → 已送达；已送达 → 已完成；已完成与已取消为终态，
     * 不允许再迁移到任何状态。</p>
     *
     * @param target 目标状态，为 {@code null} 时判定为不可流转
     * @return 允许流转返回 {@code true}，否则返回 {@code false}
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
