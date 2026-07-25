package io.ddd4j.boot.sample.richmodel.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.ddd4j.data.mybatis.repository.MybatisAggregateRepository;
import io.ddd4j.sample.richmodel.order.domain.model.Money;
import io.ddd4j.sample.richmodel.order.domain.model.Order;
import io.ddd4j.sample.richmodel.order.domain.model.OrderLine;
import io.ddd4j.sample.richmodel.order.domain.model.OrderStatus;
import io.ddd4j.sample.richmodel.order.domain.repository.OrderRepository;
import io.ddd4j.core.cqrs.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Spring Boot MyBatis adapter for the pure Order aggregate.
 *
 * <p>ddd4j 2.0.x 的 {@code MybatisAggregateRepository} 泛型为 5 个参数：
 * {@code <MP, M, P, Q, ID>}，其中 M=聚合根、P=PO、Q=Query 对象、ID=聚合标识。
 * 本示例没有独立 Query 对象，Q 使用 {@link Query}{@code <Order>} 的匿名子类占位。</p>
 */
@Repository
public class MybatisOrderRepository extends MybatisAggregateRepository<OrderTableMapper, Order, OrderTablePO, Query<Order>, String>
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
        return Optional.ofNullable(getBaseMapper().selectById(id)).map(this::toModel);
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
    public Order save(Order aggregate) {
        Objects.requireNonNull(aggregate, "aggregate must not be null");
        super.save(aggregate);
        LambdaQueryWrapper<OrderLineTablePO> query = new LambdaQueryWrapper<OrderLineTablePO>()
                .eq(OrderLineTablePO::getOrderId, aggregate.id());
        lineMapper.delete(query);
        aggregate.lines().stream()
                .map(line -> toLinePersistenceObject(aggregate.id(), line))
                .forEach(lineMapper::insert);
        return aggregate;
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
        return new OrderTablePO(
                model.id(),
                model.orderNo(),
                model.buyerId(),
                model.buyerName(),
                model.status().name(),
                totalAmount.amount(),
                totalAmount.currency()
        );
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
        return new OrderLineTablePO(
                line.id(),
                orderId,
                line.productId(),
                line.productName(),
                line.quantity(),
                line.unitPrice().amount(),
                line.unitPrice().currency()
        );
    }
}
