package io.ddd4j.boot.sample.domain.order.service;

import io.ddd4j.boot.sample.domain.order.model.aggregate.Order;
import io.ddd4j.boot.sample.domain.order.specification.OrderSpecification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单领域服务
 * 处理跨聚合的业务逻辑和领域规则
 *
 * <p>注意：领域服务应该是无状态的，不依赖Spring框架。
 * 如果需要被Spring管理，应该在infrastructure层创建实现类。</p>
 *
 * <p>领域语义：承载「不属于单个聚合根方法职责」的订单规则，
 * 如订单号生成与金额合法性校验；自身不持有状态、不做持久化，
 * 规则判定复用 {@link OrderSpecification} 以保证口径唯一。</p>
 */
public class OrderDomainService {

    /**
     * 构造订单领域服务，字段取默认值。
     */
    public OrderDomainService() {
    }


    /**
     * 验证订单是否可以取消。
     *
     * @param order 待校验的订单，可为 {@code null}
     * @return 可取消返回 {@code true}，否则返回 {@code false}
     */
    public boolean canCancel(Order order) {
        return OrderSpecification.canCancel(order);
    }

    /**
     * 验证订单是否可以支付。
     *
     * @param order 待校验的订单，可为 {@code null}
     * @return 可支付返回 {@code true}，否则返回 {@code false}
     */
    public boolean canPay(Order order) {
        return OrderSpecification.canPay(order);
    }

    /**
     * 生成订单号。
     *
     * <p>格式：ORD + 日期时间 + 随机数，例如 {@code ORD202610091530001234}；
     * 日期时间精确到秒，尾随 4 位随机数用于同秒内多单的区分。</p>
     *
     * @return 形如「ORDyyyyMMddHHmmss + 4 位随机数字」的订单号
     */
    public String generateOrderNo() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "ORD" + dateTime + random;
    }

    /**
     * 验证订单金额是否合理。
     *
     * <p>规则：订单与总金额均非空，且总金额严格大于 0。</p>
     *
     * @param order 待校验的订单，可为 {@code null}
     * @return 金额合法返回 {@code true}，否则返回 {@code false}
     */
    public boolean isAmountValid(Order order) {
        if (order == null || order.getTotalAmount() == null) {
            return false;
        }
        // 订单金额必须大于0
        return order.getTotalAmount().amount().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}
