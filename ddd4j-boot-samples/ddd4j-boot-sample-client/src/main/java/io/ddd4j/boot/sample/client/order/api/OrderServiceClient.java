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
 * @author DDD4J
 * @since 1.0.0
 */
public interface OrderServiceClient {
    
    /**
     * 创建订单
     */
    OrderResponse createOrder(CreateOrderRequest request);
    
    /**
     * 支付订单
     */
    OrderResponse payOrder(Long orderId, String paymentMethod);
    
    /**
     * 订单发货
     */
    OrderResponse shipOrder(Long orderId, String trackingNumber, String logisticsCompany);
    
    /**
     * 确认收货
     */
    OrderResponse confirmDelivery(Long orderId);
    
    /**
     * 完成订单
     */
    OrderResponse completeOrder(Long orderId);
    
    /**
     * 取消订单
     */
    OrderResponse cancelOrder(Long orderId, String reason);
    
    /**
     * 根据ID查询订单
     */
    OrderResponse getOrderById(Long id);
    
    /**
     * 根据订单号查询订单
     */
    OrderResponse getOrderByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<OrderResponse> getOrdersByUserId(Long userId);
    
    /**
     * 分页查询订单
     */
    OrderPageResponse queryOrders(OrderQueryRequest query);
}

