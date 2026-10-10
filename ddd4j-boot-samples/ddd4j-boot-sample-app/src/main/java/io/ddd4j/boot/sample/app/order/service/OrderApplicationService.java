package io.ddd4j.boot.sample.app.order.service;

import io.ddd4j.boot.sample.app.order.command.CancelOrderCommand;
import io.ddd4j.boot.sample.app.order.command.CreateOrderCommand;
import io.ddd4j.boot.sample.app.order.command.PayOrderCommand;
import io.ddd4j.boot.sample.app.order.command.ShipOrderCommand;
import io.ddd4j.boot.sample.app.order.dto.OrderDTO;
import io.ddd4j.boot.sample.app.order.mapper.OrderMapper;
import io.ddd4j.boot.sample.app.order.query.OrderQuery;
import io.ddd4j.boot.sample.app.order.response.OrderPageResponse;
import io.ddd4j.boot.sample.domain.order.model.aggregate.Order;
import io.ddd4j.boot.sample.domain.order.repository.OrderRepository;
import io.ddd4j.boot.sample.domain.order.service.OrderDomainService;
import io.ddd4j.core.exception.BizRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单应用服务
 *
 * <p>CQRS 的写侧编排入口：接收应用层命令（Command），依次完成
 * 「参数校验 → 加载订单聚合 → 调用领域行为 → 持久化并发布事件 →
 * 映射为 DTO 返回」的用例流程。所有写方法均在事务内执行，
 * 查询方法直接委托仓储并映射为读模型 DTO。</p>
 */
@Service
public class OrderApplicationService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderApplicationService.class);
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService = new OrderDomainService();
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    /**
     * 创建订单。
     *
     * <p>流程：生成订单号 → 命令映射为订单聚合（经 {@link OrderMapper#toDomain}）
     * → 附加备注 → 保存聚合 → 返回订单 DTO。</p>
     *
     * @param command 创建订单命令，含用户、收货地址、订单项与备注
     * @return 创建成功的订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 命令必填项缺失或聚合校验失败时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(CreateOrderCommand command) {
        log.info("创建订单，用户ID: {}", command.getUserId());
        // 生成订单号
        String orderNo = orderDomainService.generateOrderNo();
        // 转换为领域对象
        Order order = orderMapper.toDomain(command, orderNo);
        if (command.getRemark() != null) {
            order.setRemark(command.getRemark());
        }
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        log.info("订单创建成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 支付订单。
     *
     * <p>流程：按订单 ID 或订单号加载聚合 → 领域规则校验可支付 →
     * 执行聚合行为 {@code pay} → 保存（随事务提交发布支付事件）。</p>
     *
     * @param command 支付订单命令，含订单标识与支付方式
     * @return 支付后的订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 订单不存在或状态不允许支付时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO payOrder(PayOrderCommand command) {
        log.info("支付订单，订单号: {}", command.getOrderNo());
        Order order = findOrder(command.getOrderId(), command.getOrderNo());
        // 验证是否可以支付
        if (!orderDomainService.canPay(order)) {
            throw new BizRuntimeException("订单状态不允许支付");
        }
        // 执行支付
        order.pay(command.getPaymentMethod());
        // 保存订单（会自动发布领域事件）
        Order savedOrder = orderRepository.save(order);
        log.info("订单支付成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 订单发货。
     *
     * <p>流程：加载聚合 → 执行聚合行为 {@code ship}（记录运单号与物流公司）
     * → 保存并发布发货事件。</p>
     *
     * @param command 发货订单命令，含订单标识、物流运单号与物流公司
     * @return 发货后的订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 订单不存在或状态不允许发货时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO shipOrder(ShipOrderCommand command) {
        log.info("订单发货，订单号: {}", command.getOrderNo());
        Order order = findOrder(command.getOrderId(), command.getOrderNo());
        // 执行发货
        order.ship(command.getTrackingNumber(), command.getLogisticsCompany());
        // 保存订单（会自动发布领域事件）
        Order savedOrder = orderRepository.save(order);
        log.info("订单发货成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 确认收货。
     *
     * @param orderId 订单主键 ID，可为 {@code null}
     * @param orderNo 订单编号，{@code orderId} 为空时按其查找
     * @return 确认收货后的订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 订单不存在、两个标识均为空或状态不允许确认收货时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO confirmDelivery(Long orderId, String orderNo) {
        log.info("确认收货，订单号: {}", orderNo);
        Order order = findOrder(orderId, orderNo);
        // 执行确认收货
        order.confirmDelivery();
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        log.info("确认收货成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 完成订单（流转到已完成终态）。
     *
     * @param orderId 订单主键 ID，可为 {@code null}
     * @param orderNo 订单编号，{@code orderId} 为空时按其查找
     * @return 完成后的订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 订单不存在、两个标识均为空或状态不允许完成时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO completeOrder(Long orderId, String orderNo) {
        log.info("完成订单，订单号: {}", orderNo);
        Order order = findOrder(orderId, orderNo);
        // 执行完成订单
        order.complete();
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        log.info("订单完成，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 取消订单。
     *
     * <p>流程：加载聚合 → 领域规则校验可取消 → 执行聚合行为 {@code cancel}
     * → 保存并发布取消事件。</p>
     *
     * @param command 取消订单命令，含订单标识与取消原因
     * @return 取消后的订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 订单不存在或状态不允许取消时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO cancelOrder(CancelOrderCommand command) {
        log.info("取消订单，订单号: {}", command.getOrderNo());
        Order order = findOrder(command.getOrderId(), command.getOrderNo());
        // 验证是否可以取消
        if (!orderDomainService.canCancel(order)) {
            throw new BizRuntimeException("订单状态不允许取消");
        }
        // 执行取消
        order.cancel(command.getReason());
        // 保存订单（会自动发布领域事件）
        Order savedOrder = orderRepository.save(order);
        log.info("订单取消成功，订单号: {}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 根据ID查询订单。
     *
     * @param id 订单主键 ID
     * @return 订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 订单不存在时抛出
     */
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toDTO).orElseThrow(() -> new BizRuntimeException("订单不存在"));
    }

    /**
     * 根据订单号查询订单。
     *
     * @param orderNo 订单编号（业务单号）
     * @return 订单 DTO
     * @throws io.ddd4j.core.exception.BizRuntimeException 订单不存在时抛出
     */
    public OrderDTO getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo).map(orderMapper::toDTO).orElseThrow(() -> new BizRuntimeException("订单不存在"));
    }

    /**
     * 根据用户ID查询订单列表。
     *
     * @param userId 下单用户 ID
     * @return 订单 DTO 列表，无记录时返回空列表
     */
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }

    /**
     * 根据查询条件查询订单列表（分页）。
     *
     * <p>先校验分页参数合法性，再将应用层查询对象翻译为领域层查询对象，
     * 分别取当前页数据与总数后组装分页响应。</p>
     *
     * @param query 查询参数对象，含过滤条件与分页参数
     * @return 分页响应对象
     * @throws io.ddd4j.core.exception.BizRuntimeException 查询参数不合法（如页码或每页条数非正数）时抛出
     */
    public OrderPageResponse queryOrders(OrderQuery query) {
        // 验证查询参数
        if (!query.isValid()) {
            throw new BizRuntimeException("查询参数无效");
        }
        // 转换为领域查询对象（领域层的查询对象）
        io.ddd4j.boot.sample.domain.order.repository.OrderQuery domainQuery = new io.ddd4j.boot.sample.domain.order.repository.OrderQuery().setUserId(query.getUserId()).setStatus(query.getStatus()).setStartTime(query.getStartTime()).setEndTime(query.getEndTime()).setMinAmount(query.getMinAmount()).setMaxAmount(query.getMaxAmount()).setPageNum(query.getPageNum()).setPageSize(query.getPageSize());
        // 查询订单列表
        List<Order> orders = orderRepository.findByQuery(domainQuery);
        List<OrderDTO> orderDTOs = orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
        // 查询总数
        long total = orderRepository.countByQuery(domainQuery);
        // 构建分页响应
        return OrderPageResponse.of(orderDTOs, total, query.getPageNum(), query.getPageSize());
    }

    /**
     * 查找订单（私有辅助方法）。
     *
     * <p>优先按订单主键查找，主键为空时回退到按订单号查找。</p>
     *
     * @param orderId 订单主键 ID，可为 {@code null}
     * @param orderNo 订单编号，可为 {@code null}
     * @return 订单聚合根
     * @throws io.ddd4j.core.exception.BizRuntimeException 两个标识均为空或订单不存在时抛出
     */
    private Order findOrder(Long orderId, String orderNo) {
        if (orderId != null) {
            return orderRepository.findById(orderId).orElseThrow(() -> new BizRuntimeException("订单不存在"));
        } else if (orderNo != null) {
            return orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new BizRuntimeException("订单不存在"));
        } else {
            throw new BizRuntimeException("订单ID和订单号不能同时为空");
        }
    }

    /**
     * 构造订单应用服务。
     *
     * @param orderRepository 订单仓储，由容器注入
     */
    public OrderApplicationService(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
