package io.ddd4j.boot.sample.client.order.impl;

import io.ddd4j.boot.core.ApiRestResponse;
import io.ddd4j.boot.sample.client.order.api.OrderServiceClient;
import io.ddd4j.boot.sample.client.order.dto.request.CreateOrderRequest;
import io.ddd4j.boot.sample.client.order.dto.request.OrderQueryRequest;
import io.ddd4j.boot.sample.client.order.dto.response.OrderPageResponse;
import io.ddd4j.boot.sample.client.order.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 订单服务客户端实现（使用RestTemplate）
 * 
 * <p>使用 Spring 的 RestTemplate 实现订单服务的HTTP调用。</p>
 * 
 * <h3>配置说明：</h3>
 * <pre>{@code
 * # application.yml
 * order:
 *   service:
 *     base-url: http://localhost:8080
 * }</pre>
 * 
 * @author DDD4J
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class OrderServiceClientImpl implements OrderServiceClient {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    
    private static final String API_PREFIX = "/api/orders";
    
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("调用创建订单接口，用户ID: {}", request.getUserId());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateOrderRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {}
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderResponse payOrder(Long orderId, String paymentMethod) {
        log.debug("调用支付订单接口，订单ID: {}, 支付方式: {}", orderId, paymentMethod);
        
        PayOrderRequest request = new PayOrderRequest();
        request.setPaymentMethod(paymentMethod);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PayOrderRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/{orderId}/pay",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {},
                orderId
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderResponse shipOrder(Long orderId, String trackingNumber, String logisticsCompany) {
        log.debug("调用订单发货接口，订单ID: {}, 物流单号: {}", orderId, trackingNumber);
        
        ShipOrderRequest request = new ShipOrderRequest();
        request.setTrackingNumber(trackingNumber);
        request.setLogisticsCompany(logisticsCompany);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ShipOrderRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/{orderId}/ship",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {},
                orderId
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderResponse confirmDelivery(Long orderId) {
        log.debug("调用确认收货接口，订单ID: {}", orderId);
        
        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/{orderId}/confirm-delivery",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {},
                orderId
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderResponse completeOrder(Long orderId) {
        log.debug("调用完成订单接口，订单ID: {}", orderId);
        
        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/{orderId}/complete",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {},
                orderId
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderResponse cancelOrder(Long orderId, String reason) {
        log.debug("调用取消订单接口，订单ID: {}, 原因: {}", orderId, reason);
        
        CancelOrderRequest request = new CancelOrderRequest();
        request.setReason(reason);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CancelOrderRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/{orderId}/cancel",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {},
                orderId
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderResponse getOrderById(Long id) {
        log.debug("调用查询订单接口，订单ID: {}", id);
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/{id}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {},
                id
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderResponse getOrderByOrderNo(String orderNo) {
        log.debug("调用查询订单接口，订单号: {}", orderNo);
        
        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/order-no/{orderNo}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {},
                orderNo
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.debug("调用查询用户订单列表接口，用户ID: {}", userId);
        
        ResponseEntity<ApiRestResponse<List<OrderResponse>>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/user/{userId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiRestResponse<List<OrderResponse>>>() {},
                userId
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    @Override
    public OrderPageResponse queryOrders(OrderQueryRequest query) {
        log.debug("调用分页查询订单接口，查询条件: {}", query);
        
        if (!query.isValid()) {
            throw new IllegalArgumentException("查询参数无效");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderQueryRequest> entity = new HttpEntity<>(query, headers);
        
        ResponseEntity<ApiRestResponse<OrderPageResponse>> responseEntity = restTemplate.exchange(
                baseUrl + API_PREFIX + "/query",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiRestResponse<OrderPageResponse>>() {}
        );
        
        return handleResponse(responseEntity.getBody());
    }
    
    /**
     * 处理响应结果
     */
    private <T> T handleResponse(ApiRestResponse<T> response) {
        if (response == null) {
            throw new RuntimeException("服务调用失败，响应为空");
        }
        
        if (response.getCode() != 200 && response.getCode() != 0) {
            String message = response.getMessage() != null ? response.getMessage() : "服务调用失败";
            throw new RuntimeException(String.format("服务调用失败，错误码: %d, 错误信息: %s", response.getCode(), message));
        }
        
        return response.getData();
    }
    
    /**
     * 支付订单请求
     */
    @SuppressWarnings("unused")
    private static class PayOrderRequest {
        @SuppressWarnings("unused")
        private String paymentMethod;
        
        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }
    
    /**
     * 发货订单请求
     */
    @SuppressWarnings("unused")
    private static class ShipOrderRequest {
        @SuppressWarnings("unused")
        private String trackingNumber;
        @SuppressWarnings("unused")
        private String logisticsCompany;
        
        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }
        
        public void setLogisticsCompany(String logisticsCompany) {
            this.logisticsCompany = logisticsCompany;
        }
    }
    
    /**
     * 取消订单请求
     */
    @SuppressWarnings("unused")
    private static class CancelOrderRequest {
        @SuppressWarnings("unused")
        private String reason;
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}

