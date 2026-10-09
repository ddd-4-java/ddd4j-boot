package io.ddd4j.boot.sample.infrastructure.order.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.ddd4j.boot.sample.domain.order.event.DomainEvent;
import io.ddd4j.boot.sample.domain.order.model.aggregate.Order;
import io.ddd4j.boot.sample.domain.order.model.entity.OrderItem;
import io.ddd4j.boot.sample.domain.order.repository.OrderItemRepository;
import io.ddd4j.boot.sample.domain.order.repository.OrderQuery;
import io.ddd4j.boot.sample.domain.order.repository.OrderRepository;
import io.ddd4j.boot.sample.infrastructure.order.messaging.OrderDomainEventPublisher;
import io.ddd4j.boot.sample.infrastructure.order.persistence.converter.OrderConverter;
import io.ddd4j.boot.sample.infrastructure.order.persistence.entity.OrderEntity;
import io.ddd4j.boot.sample.infrastructure.order.persistence.entity.OrderItemEntity;
import io.ddd4j.boot.sample.infrastructure.order.persistence.mapper.OrderItemMapper;
import io.ddd4j.boot.sample.infrastructure.order.persistence.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单仓储实现（基础设施层）
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderConverter orderConverter;
    private final OrderDomainEventPublisher domainEventPublisher;

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderConverter.toEntity(order);

        if (order.getId() == null) {
            orderMapper.insert(entity);
            order.setId(entity.getId());
        } else {
            orderMapper.updateById(entity);
        }

        // 保存订单项
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            // 删除旧的订单项
            orderItemMapper.delete(new LambdaQueryWrapper<OrderItemEntity>()
                    .eq(OrderItemEntity::getOrderId, order.getId()));

            // 保存新的订单项
            for (OrderItem item : order.getItems()) {
                item.setOrderId(order.getId());
                OrderItemEntity itemEntity = orderConverter.toItemEntity(item);
                if (item.getId() == null) {
                    orderItemMapper.insert(itemEntity);
                    item.setId(itemEntity.getId());
                } else {
                    orderItemMapper.updateById(itemEntity);
                }
            }
        }

        // 重新加载订单项
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        Order savedOrder = orderConverter.toDomain(entity, items);

        // 发布领域事件
        List<DomainEvent> events = order.getDomainEvents();
        if (events != null && !events.isEmpty()) {
            // 更新事件中的订单ID（如果是新创建的订单）
            for (DomainEvent event : events) {
                if (event instanceof io.ddd4j.boot.sample.domain.order.event.OrderCreatedEvent) {
                    io.ddd4j.boot.sample.domain.order.event.OrderCreatedEvent createdEvent =
                            (io.ddd4j.boot.sample.domain.order.event.OrderCreatedEvent) event;
                    if (createdEvent.getOrderId() == null) {
                        // 创建新事件，包含订单ID
                        domainEventPublisher.publish(new io.ddd4j.boot.sample.domain.order.event.OrderCreatedEvent(
                                savedOrder.getId(), savedOrder.getOrderNo(), savedOrder.getUserId(),
                                savedOrder.getTotalAmount().amount().toString()));
                    } else {
                        domainEventPublisher.publish(event);
                    }
                } else {
                    domainEventPublisher.publish(event);
                }
            }
            // 清空领域事件
            order.clearDomainEvents();
        }

        return savedOrder;
    }

    @Override
    public Optional<Order> findById(Long id) {
        OrderEntity entity = orderMapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        return Optional.of(orderConverter.toDomain(entity, items));
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        OrderEntity entity = orderMapper.selectOne(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderNo, orderNo));

        if (entity == null) {
            return Optional.empty();
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(entity.getId());
        return Optional.of(orderConverter.toDomain(entity, items));
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        List<OrderEntity> entities = orderMapper.selectList(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getUserId, userId)
                .orderByDesc(OrderEntity::getId));

        return entities.stream()
                .map(entity -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(entity.getId());
                    return orderConverter.toDomain(entity, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByQuery(OrderQuery query) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();

        if (query.getUserId() != null) {
            wrapper.eq(OrderEntity::getUserId, query.getUserId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(OrderEntity::getStatus, query.getStatus().getCode());
        }
        if (query.getStartTime() != null) {
            wrapper.ge(OrderEntity::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(OrderEntity::getCreateTime, query.getEndTime());
        }
        if (query.getMinAmount() != null) {
            wrapper.ge(OrderEntity::getTotalAmount, query.getMinAmount());
        }
        if (query.getMaxAmount() != null) {
            wrapper.le(OrderEntity::getTotalAmount, query.getMaxAmount());
        }

        wrapper.orderByDesc(OrderEntity::getCreateTime);

        // 分页查询
        Page<OrderEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<OrderEntity> pageResult = orderMapper.selectPage(page, wrapper);

        return pageResult.getRecords().stream()
                .map(entity -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(entity.getId());
                    return orderConverter.toDomain(entity, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public long countByQuery(OrderQuery query) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();

        if (query.getUserId() != null) {
            wrapper.eq(OrderEntity::getUserId, query.getUserId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(OrderEntity::getStatus, query.getStatus().getCode());
        }
        if (query.getStartTime() != null) {
            wrapper.ge(OrderEntity::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(OrderEntity::getCreateTime, query.getEndTime());
        }
        if (query.getMinAmount() != null) {
            wrapper.ge(OrderEntity::getTotalAmount, query.getMinAmount());
        }
        if (query.getMaxAmount() != null) {
            wrapper.le(OrderEntity::getTotalAmount, query.getMaxAmount());
        }

        return orderMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Long id) {
        // 删除订单项
        orderItemRepository.deleteByOrderId(id);
        // 删除订单
        orderMapper.deleteById(id);
    }

}

