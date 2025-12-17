package io.ddd4j.boot.sample.order.infrastructure.persistence.converter;

import io.ddd4j.boot.sample.order.domain.model.aggregate.Order;
import io.ddd4j.boot.sample.order.domain.model.entity.OrderItem;
import io.ddd4j.boot.sample.order.domain.model.vo.Address;
import io.ddd4j.boot.sample.order.domain.model.vo.Money;
import io.ddd4j.boot.sample.order.domain.model.vo.OrderStatus;
import io.ddd4j.boot.sample.order.infrastructure.persistence.entity.OrderEntity;
import io.ddd4j.boot.sample.order.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单转换器（领域对象与持久化实体转换）
 */
@Component
public class OrderConverter {
    
    /**
     * 领域对象转持久化实体
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
        entity.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount().getAmount() : null);
        entity.setCurrency(order.getTotalAmount() != null ? order.getTotalAmount().getCurrency() : null);
        
        if (order.getShippingAddress() != null) {
            Address address = order.getShippingAddress();
            entity.setProvince(address.getProvince());
            entity.setCity(address.getCity());
            entity.setDistrict(address.getDistrict());
            entity.setDetail(address.getDetail());
            entity.setZipCode(address.getZipCode());
        }
        
        entity.setRemark(order.getRemark());
        entity.setPaidTime(order.getPaidTime());
        entity.setShippedTime(order.getShippedTime());
        entity.setDeliveredTime(order.getDeliveredTime());
        
        return entity;
    }
    
    /**
     * 持久化实体转领域对象
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
     * 订单项领域对象转持久化实体
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
        entity.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().getAmount() : null);
        entity.setTotalPrice(item.getTotalPrice() != null ? item.getTotalPrice().getAmount() : null);
        entity.setCurrency(item.getUnitPrice() != null ? item.getUnitPrice().getCurrency() : null);
        
        return entity;
    }
    
    /**
     * 订单项持久化实体转领域对象
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
     * 订单项列表转换
     */
    public List<OrderItem> toItemDomainList(List<OrderItemEntity> entities) {
        if (entities == null) {
            return new java.util.ArrayList<>();
        }
        return entities.stream()
                .map(this::toItemDomain)
                .collect(Collectors.toList());
    }
    
    public List<OrderItemEntity> toItemEntityList(List<OrderItem> items) {
        if (items == null) {
            return new java.util.ArrayList<>();
        }
        return items.stream()
                .map(this::toItemEntity)
                .collect(Collectors.toList());
    }
}

