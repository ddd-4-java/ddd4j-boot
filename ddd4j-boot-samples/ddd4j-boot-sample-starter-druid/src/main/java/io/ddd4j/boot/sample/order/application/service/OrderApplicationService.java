package io.ddd4j.boot.sample.order.application.service;

import io.ddd4j.boot.sample.order.application.command.CancelOrderCommand;
import io.ddd4j.boot.sample.order.application.command.CreateOrderCommand;
import io.ddd4j.boot.sample.order.application.command.PayOrderCommand;
import io.ddd4j.boot.sample.order.application.command.ShipOrderCommand;
import io.ddd4j.boot.sample.order.application.dto.OrderDTO;
import io.ddd4j.boot.sample.order.application.mapper.OrderMapper;
import io.ddd4j.boot.sample.order.application.query.OrderQuery;
import io.ddd4j.boot.sample.order.application.response.OrderPageResponse;
import io.ddd4j.boot.sample.order.domain.model.aggregate.Order;
import io.ddd4j.boot.sample.order.domain.repository.OrderRepository;
import io.ddd4j.boot.sample.order.domain.service.OrderDomainService;
import io.ddd4j.core.exception.BizRuntimeException;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单应用服务
 */

@Service

public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    public OrderApplicationService(OrderRepository orderRepository, OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
    }

    /**
     * 创建订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(CreateOrderCommand command) {
        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("创建订单，用户ID: {}", command.getUserId());

        // 生成订单号
        String orderNo = orderDomainService.generateOrderNo();

        // 转换为领域对象
        Order order = orderMapper.toDomain(command, orderNo);
        order.setRemark(command.getRemark());

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("订单创建成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 支付订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO payOrder(PayOrderCommand command) {
        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("支付订单，订单号: {}", command.getOrderNo());

        Order order = findOrder(command.getOrderId(), command.getOrderNo());

        // 验证是否可以支付
        if (!orderDomainService.canPay(order)) {
            throw new BizRuntimeException("订单状态不允许支付");
        }

        // 执行支付
        order.pay(command.getPaymentMethod());

        // 保存订单（会自动发布领域事件）
        Order savedOrder = orderRepository.save(order);

        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("订单支付成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 发货
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO shipOrder(ShipOrderCommand command) {
        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("订单发货，订单号: {}", command.getOrderNo());

        Order order = findOrder(command.getOrderId(), command.getOrderNo());

        // 执行发货
        order.ship(command.getTrackingNumber(), command.getLogisticsCompany());

        // 保存订单（会自动发布领域事件）
        Order savedOrder = orderRepository.save(order);

        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("订单发货成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 确认收货
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO confirmDelivery(Long orderId, String orderNo) {
        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("确认收货，订单号: {}", orderNo);

        Order order = findOrder(orderId, orderNo);

        // 执行确认收货
        order.confirmDelivery();

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("确认收货成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 完成订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO completeOrder(Long orderId, String orderNo) {
        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("完成订单，订单号: {}", orderNo);

        Order order = findOrder(orderId, orderNo);

        // 执行完成订单
        order.complete();

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("订单完成，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO cancelOrder(CancelOrderCommand command) {
        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("取消订单，订单号: {}", command.getOrderNo());

        Order order = findOrder(command.getOrderId(), command.getOrderNo());

        // 验证是否可以取消
        if (!orderDomainService.canCancel(order)) {
            throw new BizRuntimeException("订单状态不允许取消");
        }

        // 执行取消
        order.cancel(command.getReason());

        // 保存订单（会自动发布领域事件）
        Order savedOrder = orderRepository.save(order);

        org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class).info("订单取消成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 根据ID查询订单
     */
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new BizRuntimeException("订单不存在"));
    }

    /**
     * 根据订单号查询订单
     */
    public OrderDTO getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new BizRuntimeException("订单不存在"));
    }

    /**
     * 根据用户ID查询订单列表
     */
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据查询条件查询订单列表（分页）
     *
     * @param query 查询参数对象
     * @return 分页响应对象
     */
    public OrderPageResponse queryOrders(OrderQuery query) {
        // 验证查询参数
        if (!query.isValid()) {
            throw new BizRuntimeException("查询参数无效");
        }

        // 转换为领域查询对象（领域层的查询对象）
        io.ddd4j.boot.sample.order.domain.repository.OrderQuery domainQuery =
                new io.ddd4j.boot.sample.order.domain.repository.OrderQuery()
                        .setUserId(query.getUserId())
                        .setStatus(query.getStatus())
                        .setStartTime(query.getStartTime())
                        .setEndTime(query.getEndTime())
                        .setMinAmount(query.getMinAmount())
                        .setMaxAmount(query.getMaxAmount())
                        .setPageNum(query.getPageNum())
                        .setPageSize(query.getPageSize());

        // 查询订单列表
        List<Order> orders = orderRepository.findByQuery(domainQuery);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());

        // 查询总数
        long total = orderRepository.countByQuery(domainQuery);

        // 构建分页响应
        return OrderPageResponse.of(orderDTOs, total, query.getPageNum(), query.getPageSize());
    }

    /**
     * 查找订单
     */
    private Order findOrder(Long orderId, String orderNo) {
        if (orderId != null) {
            return orderRepository.findById(orderId)
                    .orElseThrow(() -> new BizRuntimeException("订单不存在"));
        } else if (orderNo != null) {
            return orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new BizRuntimeException("订单不存在"));
        } else {
            throw new BizRuntimeException("订单ID和订单号不能同时为空");
        }
    }
}

