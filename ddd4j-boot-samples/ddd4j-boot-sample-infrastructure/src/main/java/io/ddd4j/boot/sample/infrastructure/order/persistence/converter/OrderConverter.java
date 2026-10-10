package io.ddd4j.boot.sample.infrastructure.order.persistence.converter;

import io.ddd4j.boot.sample.domain.order.model.aggregate.Order;
import io.ddd4j.boot.sample.domain.order.model.entity.OrderItem;
import io.ddd4j.boot.sample.domain.order.model.vo.Address;
import io.ddd4j.boot.sample.domain.order.model.vo.Money;
import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;
import io.ddd4j.boot.sample.infrastructure.order.persistence.entity.OrderEntity;
import io.ddd4j.boot.sample.infrastructure.order.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单转换器（领域对象与持久化实体转换）
 *
 * <p>领域语义：防腐层（ACL）中的对象映射组件，负责订单聚合与
 * t_order / t_order_item 表实体之间的双向转换，隔离领域模型与
 * 存储模型；状态编码、金额与币种、地址拆合等差异均在此收敛，
 * 转换失败（入参为 null）一律返回 {@code null} 而非抛错。</p>
 */
@Component
public class OrderConverter {

    /**
     * 构造订单转换器，本类无状态、可复用。
     */
    public OrderConverter() {
    }

    /**
     * 领域对象转持久化实体。
     *
     * <p>状态取编码值、金额与币种拆分存储、地址打平为省市区等列。</p>
     *
     * @param order 订单聚合根，为 {@code null} 时返回 {@code null}
     * @return 对应的订单持久化实体
     */
    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setOrderNo(order.getOrderNo());
        entity.setUserId(order.getUserId());
        entity.setStatus(order.getStatus() != null ? order.getStatus().getCode() : null);
        entity.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount().amount() : null);
        entity.setCurrency(order.getTotalAmount() != null ? order.getTotalAmount().currency() : null);

        if (order.getShippingAddress() != null) {
            Address address = order.getShippingAddress();
            entity.setProvince(address.province());
            entity.setCity(address.city());
            entity.setDistrict(address.district());
            entity.setDetail(address.detail());
            entity.setZipCode(address.zipCode());
        }

        entity.setRemark(order.getRemark());
        entity.setPaidTime(order.getPaidTime());
        entity.setShippedTime(order.getShippedTime());
        entity.setDeliveredTime(order.getDeliveredTime());

        return entity;
    }

    /**
     * 持久化实体转领域对象。
     *
     * <p>状态编码还原为 {@link OrderStatus}，省市区各列还原为
     * {@link Address} 值对象，币种缺失时按 CNY 兜底；
     * 订单项由调用方一并传入后挂载到聚合内部。</p>
     *
     * @param entity 订单持久化实体，为 {@code null} 时返回 {@code null}
     * @param items  该订单的订单项列表，为 {@code null} 时按空列表处理
     * @return 订单聚合根实例
     * @throws IllegalArgumentException 状态编码无法匹配到 {@link OrderStatus} 时抛出
     */
    public Order toDomain(OrderEntity entity, List<OrderItem> items) {
        if (entity == null) {
            return null;
        }

        Order order = new Order();
        order.setId(entity.getId());
        order.setOrderNo(entity.getOrderNo());
        order.setUserId(entity.getUserId());
        order.setStatus(OrderStatus.valueOf(entity.getStatus()));
        order.setTotalAmount(new Money(entity.getTotalAmount(), entity.getCurrency() != null ? entity.getCurrency() : "CNY"));

        if (entity.getProvince() != null) {
            order.setShippingAddress(new Address(
                    entity.getProvince(),
                    entity.getCity(),
                    entity.getDistrict(),
                    entity.getDetail(),
                    entity.getZipCode()
            ));
        }

        order.setRemark(entity.getRemark());
        order.setPaidTime(entity.getPaidTime());
        order.setShippedTime(entity.getShippedTime());
        order.setDeliveredTime(entity.getDeliveredTime());
        order.setItems(items != null ? items : new java.util.ArrayList<>());

        return order;
    }

    /**
     * 订单项领域对象转持久化实体。
     *
     * <p>单价与小计的金额部分写入数值列，币种单独落列。</p>
     *
     * @param item 订单项实体，为 {@code null} 时返回 {@code null}
     * @return 对应的订单项持久化实体
     */
    public OrderItemEntity toItemEntity(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(item.getId());
        entity.setOrderId(item.getOrderId());
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().amount() : null);
        entity.setTotalPrice(item.getTotalPrice() != null ? item.getTotalPrice().amount() : null);
        entity.setCurrency(item.getUnitPrice() != null ? item.getUnitPrice().currency() : null);

        return entity;
    }

    /**
     * 订单项持久化实体转领域对象。
     *
     * <p>金额列与币种列重组为 {@link Money} 后，走订单项构造器
     * 复用其必填校验与小计推导逻辑。</p>
     *
     * @param entity 订单项持久化实体，为 {@code null} 时返回 {@code null}
     * @return 订单项领域对象
     * @throws IllegalArgumentException 商品 ID、数量或单价非法（持久化数据不合规）时抛出
     */
    public OrderItem toItemDomain(OrderItemEntity entity) {
        if (entity == null) {
            return null;
        }

        OrderItem item = new OrderItem(
                entity.getProductId(),
                entity.getProductName(),
                entity.getQuantity(),
                new Money(entity.getUnitPrice(), entity.getCurrency() != null ? entity.getCurrency() : "CNY")
        );
        item.setId(entity.getId());
        item.setOrderId(entity.getOrderId());

        return item;
    }

    /**
     * 订单项持久化实体列表批量转领域对象列表。
     *
     * @param entities 订单项持久化实体列表，为 {@code null} 时返回空列表
     * @return 订单项领域对象列表
     */
    public List<OrderItem> toItemDomainList(List<OrderItemEntity> entities) {
        if (entities == null) {
            return new java.util.ArrayList<>();
        }
        return entities.stream()
                .map(this::toItemDomain)
                .collect(Collectors.toList());
    }

    /**
     * 订单项领域对象列表批量转持久化实体列表。
     *
     * @param items 订单项领域对象列表，为 {@code null} 时返回空列表
     * @return 订单项持久化实体列表
     */
    public List<OrderItemEntity> toItemEntityList(List<OrderItem> items) {
        if (items == null) {
            return new java.util.ArrayList<>();
        }
        return items.stream()
                .map(this::toItemEntity)
                .collect(Collectors.toList());
    }
}

