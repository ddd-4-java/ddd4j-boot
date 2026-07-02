package io.ddd4j.boot.sample.richmodel.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.ddd4j.data.mybatis.repository.MybatisAggregateRepository;
import io.ddd4j.sample.richmodel.order.domain.model.Money;
import io.ddd4j.sample.richmodel.order.domain.model.Order;
import io.ddd4j.sample.richmodel.order.domain.model.OrderLine;
import io.ddd4j.sample.richmodel.order.domain.model.OrderStatus;
import io.ddd4j.sample.richmodel.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Spring Boot MyBatis adapter for the pure Order aggregate.
 */
@Repository
public class MybatisOrderRepository extends MybatisAggregateRepository<Order, OrderTablePO, String>
        implements OrderRepository {

    private final OrderLineTableMapper lineMapper;

    public MybatisOrderRepository(OrderTableMapper mapper, OrderLineTableMapper lineMapper) {
        super(mapper);
        this.lineMapper = Objects.requireNonNull(lineMapper, "lineMapper must not be null");
    }

    @Override
    public Optional<Order> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(mapper().selectById(id)).map(this::toModel);
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            return Optional.empty();
        }
        return Optional.ofNullable(lambdaQuery()
                .eq(OrderTablePO::getOrderNo, orderNo)
                .one())
                .map(this::toModel);
    }

    @Override
    public void save(Order aggregate) {
        Objects.requireNonNull(aggregate, "aggregate must not be null");
        super.save(aggregate);
        LambdaQueryWrapper<OrderLineTablePO> query = new LambdaQueryWrapper<OrderLineTablePO>()
                .eq(OrderLineTablePO::getOrderId, aggregate.id());
        lineMapper.delete(query);
        aggregate.lines().stream()
                .map(line -> toLinePersistenceObject(aggregate.id(), line))
                .forEach(lineMapper::insert);
    }

    @Override
    public Order toModel(OrderTablePO persistenceObject) {
        Objects.requireNonNull(persistenceObject, "persistenceObject must not be null");
        List<OrderLine> lines = lineMapper.selectList(new LambdaQueryWrapper<OrderLineTablePO>()
                        .eq(OrderLineTablePO::getOrderId, persistenceObject.getId()))
                .stream()
                .map(this::toLineModel)
                .toList();
        return new Order(
                persistenceObject.getId(),
                persistenceObject.getOrderNo(),
                persistenceObject.getBuyerId(),
                persistenceObject.getBuyerName(),
                OrderStatus.valueOf(persistenceObject.getStatus()),
                lines
        );
    }

    @Override
    public OrderTablePO toPersistenceObject(Order model) {
        Objects.requireNonNull(model, "model must not be null");
        Money totalAmount = model.totalAmount();
        return OrderTablePO.builder()
                .id(model.id())
                .orderNo(model.orderNo())
                .buyerId(model.buyerId())
                .buyerName(model.buyerName())
                .status(model.status().name())
                .totalAmount(totalAmount.amount())
                .currency(totalAmount.currency())
                .build();
    }

    private OrderLine toLineModel(OrderLineTablePO persistenceObject) {
        Objects.requireNonNull(persistenceObject, "persistenceObject must not be null");
        return new OrderLine(
                persistenceObject.getId(),
                persistenceObject.getProductId(),
                persistenceObject.getProductName(),
                persistenceObject.getQuantity(),
                new Money(persistenceObject.getUnitPrice(), persistenceObject.getCurrency())
        );
    }

    private OrderLineTablePO toLinePersistenceObject(String orderId, OrderLine line) {
        Objects.requireNonNull(line, "line must not be null");
        return OrderLineTablePO.builder()
                .id(line.id())
                .orderId(orderId)
                .productId(line.productId())
                .productName(line.productName())
                .quantity(line.quantity())
                .unitPrice(line.unitPrice().amount())
                .currency(line.unitPrice().currency())
                .build();
    }
}
