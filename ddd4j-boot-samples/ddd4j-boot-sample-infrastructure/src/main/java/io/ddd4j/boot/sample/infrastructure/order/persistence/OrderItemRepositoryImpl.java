package io.ddd4j.boot.sample.infrastructure.order.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.ddd4j.boot.sample.domain.order.model.entity.OrderItem;
import io.ddd4j.boot.sample.domain.order.repository.OrderItemRepository;
import io.ddd4j.boot.sample.infrastructure.order.persistence.converter.OrderConverter;
import io.ddd4j.boot.sample.infrastructure.order.persistence.entity.OrderItemEntity;
import io.ddd4j.boot.sample.infrastructure.order.persistence.mapper.OrderItemMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单项仓储实现（基础设施层）
 *
 * <p>领域语义：领域层 {@link OrderItemRepository} 接口的 MyBatis-Plus 实现，
 * 以 LambdaQueryWrapper 组装按订单维度的查询与级联删除，并通过
 * {@link OrderConverter} 完成实体与领域对象的双向转换。</p>
 */
@Repository
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
        return orderItems.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        List<OrderItemEntity> entities = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, orderId));
        return orderConverter.toItemDomainList(entities);
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, orderId));
    }

    @Override
    public void delete(Long id) {
        orderItemMapper.deleteById(id);
    }

    /**
     * 构造订单项仓储实现。
     *
     * @param orderItemMapper 订单项 MyBatis-Plus Mapper，由容器注入
     * @param orderConverter  订单对象转换器，由容器注入
     */
    public OrderItemRepositoryImpl(final OrderItemMapper orderItemMapper, final OrderConverter orderConverter) {
        this.orderItemMapper = orderItemMapper;
        this.orderConverter = orderConverter;
    }
}
