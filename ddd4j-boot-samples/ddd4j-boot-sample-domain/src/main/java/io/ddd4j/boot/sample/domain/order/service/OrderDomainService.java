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
 */
public class OrderDomainService {
    
    /**
     * 验证订单是否可以取消
     */
    public boolean canCancel(Order order) {
        return OrderSpecification.canCancel(order);
    }
    
    /**
     * 验证订单是否可以支付
     */
    public boolean canPay(Order order) {
        return OrderSpecification.canPay(order);
    }
    
    /**
     * 生成订单号
     * 格式：ORD + 日期时间 + 随机数
     */
    public String generateOrderNo() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "ORD" + dateTime + random;
    }
    
    /**
     * 验证订单金额是否合理
     */
    public boolean isAmountValid(Order order) {
        if (order == null || order.getTotalAmount() == null) {
            return false;
        }
        // 订单金额必须大于0
        return order.getTotalAmount().amount().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}

