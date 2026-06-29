package io.ddd4j.boot.sample.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.ddd4j.boot.sample.order.domain.model.entity.OrderItem;
import io.ddd4j.boot.sample.order.domain.repository.OrderItemRepository;
import io.ddd4j.boot.sample.order.infrastructure.persistence.converter.OrderConverter;
import io.ddd4j.boot.sample.order.infrastructure.persistence.entity.OrderItemEntity;
import io.ddd4j.boot.sample.order.infrastructure.persistence.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单项仓储实现（基础设施层）
 */
@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemMapper orderItemMapper;
    private final OrderConverter orderConverter;

    @Override
    public OrderItem save(OrderItem orderItem) {
        OrderItemEntity entity = orderConverter.toItemEntity(orderItem);

        if (orderItem.getId() == null) {
            orderItemMapper.insert(entity);
            orderItem.setId(entity.getId());
        } else {
            orderItemMapper.updateById(entity);
        }

        return orderItem;
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        List<OrderItemEntity> entities = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>()
                        .eq(OrderItemEntity::getOrderId, orderId));

        return orderConverter.toItemDomainList(entities);
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItemEntity>()
                .eq(OrderItemEntity::getOrderId, orderId));
    }

    @Override
    public void delete(Long id) {
        orderItemMapper.deleteById(id);
    }

}

