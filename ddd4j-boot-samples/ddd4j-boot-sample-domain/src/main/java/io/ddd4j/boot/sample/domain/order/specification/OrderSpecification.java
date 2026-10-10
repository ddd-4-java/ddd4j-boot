package io.ddd4j.boot.sample.domain.order.specification;

import io.ddd4j.boot.sample.domain.order.model.aggregate.Order;
import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;

import java.time.LocalDateTime;

/**
 * 订单规格（Specification Pattern）
 * 用于封装复杂的业务规则查询条件
 *
 * <p>领域语义：把「订单当前状态是否允许某动作」「订单是否落在某个统计区间」
 * 这类判断从业务流程中抽离为无状态的静态谓词，供应用服务、领域服务与
 * 对外查询统一复用，避免状态判定逻辑散落各处而产生不一致。</p>
 */
public class OrderSpecification {

    /**
     * 构造订单规格，字段取默认值。
     */
    public OrderSpecification() {
    }


    /**
     * 判断订单当前是否可以取消。
     *
     * <p>仅待支付（PENDING）与已支付（PAID）状态允许取消；
     * 订单为空视为不可取消。</p>
     *
     * @param order 待判定的订单，可为 {@code null}
     * @return 可以取消返回 {@code true}，否则返回 {@code false}
     */
    public static boolean canCancel(Order order) {
        if (order == null) {
            return false;
        }
        OrderStatus status = order.getStatus();
        return status == OrderStatus.PENDING || status == OrderStatus.PAID;
    }

    /**
     * 判断订单当前是否可以支付。
     *
     * @param order 待判定的订单，可为 {@code null}
     * @return 仅待支付状态且订单非空时返回 {@code true}，否则返回 {@code false}
     */
    public static boolean canPay(Order order) {
        return order != null && order.getStatus() == OrderStatus.PENDING;
    }

    /**
     * 判断订单当前是否可以发货。
     *
     * @param order 待判定的订单，可为 {@code null}
     * @return 仅已支付状态且订单非空时返回 {@code true}，否则返回 {@code false}
     */
    public static boolean canShip(Order order) {
        return order != null && order.getStatus() == OrderStatus.PAID;
    }

    /**
     * 判断订单当前是否可以确认收货。
     *
     * @param order 待判定的订单，可为 {@code null}
     * @return 仅已发货状态且订单非空时返回 {@code true}，否则返回 {@code false}
     */
    public static boolean canConfirmDelivery(Order order) {
        return order != null && order.getStatus() == OrderStatus.SHIPPED;
    }

    /**
     * 判断订单当前是否可以完成（终态流转）。
     *
     * @param order 待判定的订单，可为 {@code null}
     * @return 仅已送达状态且订单非空时返回 {@code true}，否则返回 {@code false}
     */
    public static boolean canComplete(Order order) {
        return order != null && order.getStatus() == OrderStatus.DELIVERED;
    }

    /**
     * 判断订单创建时间是否落在指定区间内（闭区间）。
     *
     * <p>{@code start} 或 {@code end} 为 {@code null} 表示该侧不设边界，
     * 即半开区间查询；订单为空或创建时间缺失一律判为不命中。</p>
     *
     * @param order 待判定的订单，可为 {@code null}
     * @param start 区间起始时间（含），为 {@code null} 时不校验下界
     * @param end   区间结束时间（含），为 {@code null} 时不校验上界
     * @return 落在区间内返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isCreatedBetween(Order order, LocalDateTime start, LocalDateTime end) {
        if (order == null || order.getCreateTime() == null) {
            return false;
        }
        LocalDateTime createTime = order.getCreateTime();
        return (start == null || !createTime.isBefore(start))
                && (end == null || !createTime.isAfter(end));
    }

    /**
     * 判断订单总金额是否严格大于指定金额。
     *
     * <p>用于「大额订单」这类统计口径；订单为空或总金额缺失判为不命中。</p>
     *
     * @param order  待判定的订单，可为 {@code null}
     * @param amount 比较基准金额，方法内按 {@link java.math.BigDecimal#compareTo} 语义比较
     * @return 总金额大于基准金额返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isAmountGreaterThan(Order order, java.math.BigDecimal amount) {
        return order != null
                && order.getTotalAmount() != null
                && order.getTotalAmount().amount().compareTo(amount) > 0;
    }
}
