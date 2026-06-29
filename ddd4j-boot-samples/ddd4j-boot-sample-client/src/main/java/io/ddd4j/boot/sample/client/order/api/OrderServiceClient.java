package io.ddd4j.boot.sample.client.order.api;

import io.ddd4j.boot.sample.client.order.dto.request.CreateOrderRequest;
import io.ddd4j.boot.sample.client.order.dto.request.OrderQueryRequest;
import io.ddd4j.boot.sample.client.order.dto.response.OrderPageResponse;
import io.ddd4j.boot.sample.client.order.dto.response.OrderResponse;

import java.util.List;

/**
 * 订单服务客户端接口（SDK）
 *
 * <p>定义订单服务对外提供的客户端接口。
 * 其他服务或客户端可以通过此接口调用订单服务。</p>
 *
 * <p>此接口遵循以下原则：
 * <ul>
 *   <li>接口定义清晰，易于理解</li>
 *   <li>使用独立的DTO对象，不依赖内部实现</li>
 *   <li>支持同步和异步调用（可根据需要扩展）</li>
 *   <li>提供完整的文档说明</li>
 * </ul>
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 注入客户端实现
 * @Autowired
 * private OrderServiceClient orderServiceClient;
 *
 * // 创建订单
 * CreateOrderRequest request = new CreateOrderRequest();
 * request.setUserId(1001L);
 * // ... 设置其他参数
 * OrderResponse order = orderServiceClient.createOrder(request);
 *
 * // 查询订单
 * OrderResponse order = orderServiceClient.getOrderById(1L);
 *
 * // 分页查询
 * OrderQueryRequest query = new OrderQueryRequest();
 * query.setUserId(1001L);
 * OrderPageResponse page = orderServiceClient.queryOrders(query);
 * }</pre>
 *
 * @author DDD4J
 * @since 1.0.0
 */
public interface OrderServiceClient {

    /**
     * 创建订单
     *
     * @param request 创建订单请求
     * @return 订单信息
     * @throws IllegalArgumentException 参数验证失败
     * @throws RuntimeException         业务异常或系统异常
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * 支付订单
     *
     * @param orderId       订单ID
     * @param paymentMethod 支付方式
     * @return 订单信息
     * @throws IllegalArgumentException 参数验证失败
     * @throws RuntimeException         业务异常或系统异常
     */
    OrderResponse payOrder(Long orderId, String paymentMethod);

    /**
     * 订单发货
     *
     * @param orderId          订单ID
     * @param trackingNumber   物流单号
     * @param logisticsCompany 物流公司
     * @return 订单信息
     * @throws IllegalArgumentException 参数验证失败
     * @throws RuntimeException         业务异常或系统异常
     */
    OrderResponse shipOrder(Long orderId, String trackingNumber, String logisticsCompany);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return 订单信息
     * @throws IllegalArgumentException 参数验证失败
     * @throws RuntimeException         业务异常或系统异常
     */
    OrderResponse confirmDelivery(Long orderId);

    /**
     * 完成订单
     *
     * @param orderId 订单ID
     * @return 订单信息
     * @throws IllegalArgumentException 参数验证失败
     * @throws RuntimeException         业务异常或系统异常
     */
    OrderResponse completeOrder(Long orderId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param reason  取消原因
     * @return 订单信息
     * @throws IllegalArgumentException 参数验证失败
     * @throws RuntimeException         业务异常或系统异常
     */
    OrderResponse cancelOrder(Long orderId, String reason);

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单信息，如果不存在返回null
     */
    OrderResponse getOrderById(Long id);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单信息，如果不存在返回null
     */
    OrderResponse getOrderByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderResponse> getOrdersByUserId(Long userId);

    /**
     * 分页查询订单
     *
     * @param query 查询条件
     * @return 分页响应
     * @throws IllegalArgumentException 查询参数无效
     */
    OrderPageResponse queryOrders(OrderQueryRequest query);
}

