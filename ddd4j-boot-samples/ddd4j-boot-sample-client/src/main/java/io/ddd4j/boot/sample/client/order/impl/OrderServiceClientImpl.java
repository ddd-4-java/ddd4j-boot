package io.ddd4j.boot.sample.client.order.impl;

import io.ddd4j.boot.sample.client.order.api.OrderServiceClient;
import io.ddd4j.boot.sample.client.order.dto.request.CreateOrderRequest;
import io.ddd4j.boot.sample.client.order.dto.request.OrderQueryRequest;
import io.ddd4j.boot.sample.client.order.dto.response.OrderPageResponse;
import io.ddd4j.boot.sample.client.order.dto.response.OrderResponse;
import io.ddd4j.core.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * 订单服务客户端实现（使用RestClient）
 *
 * <p>使用Spring 6.1+ 的 RestClient 实现订单服务的HTTP调用。</p>
 *
 * <b>配置说明：</b>
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
public class OrderServiceClientImpl implements OrderServiceClient {

    private static final String API_PREFIX = "/api/orders";
    private static final Logger log = LoggerFactory.getLogger(OrderServiceClientImpl.class);
    private final RestClient restClient;
    private final String baseUrl;

    public OrderServiceClientImpl(RestClient restClient, String baseUrl) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("调用创建订单接口，用户ID: {}", request.getUserId());

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.post()
                .uri(baseUrl + API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderResponse payOrder(Long orderId, String paymentMethod) {
        log.debug("调用支付订单接口，订单ID: {}, 支付方式: {}", orderId, paymentMethod);

        PayOrderRequest request = new PayOrderRequest();
        request.setPaymentMethod(paymentMethod);

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.post()
                .uri(baseUrl + API_PREFIX + "/{orderId}/pay", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderResponse shipOrder(Long orderId, String trackingNumber, String logisticsCompany) {
        log.debug("调用订单发货接口，订单ID: {}, 物流单号: {}", orderId, trackingNumber);

        ShipOrderRequest request = new ShipOrderRequest();
        request.setTrackingNumber(trackingNumber);
        request.setLogisticsCompany(logisticsCompany);

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.post()
                .uri(baseUrl + API_PREFIX + "/{orderId}/ship", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderResponse confirmDelivery(Long orderId) {
        log.debug("调用确认收货接口，订单ID: {}", orderId);

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.post()
                .uri(baseUrl + API_PREFIX + "/{orderId}/confirm-delivery", orderId)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderResponse completeOrder(Long orderId) {
        log.debug("调用完成订单接口，订单ID: {}", orderId);

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.post()
                .uri(baseUrl + API_PREFIX + "/{orderId}/complete", orderId)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, String reason) {
        log.debug("调用取消订单接口，订单ID: {}, 原因: {}", orderId, reason);

        CancelOrderRequest request = new CancelOrderRequest();
        request.setReason(reason);

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.post()
                .uri(baseUrl + API_PREFIX + "/{orderId}/cancel", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        log.debug("调用查询订单接口，订单ID: {}", id);

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.get()
                .uri(baseUrl + API_PREFIX + "/{id}", id)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderResponse getOrderByOrderNo(String orderNo) {
        log.debug("调用查询订单接口，订单号: {}", orderNo);

        ResponseEntity<ApiRestResponse<OrderResponse>> responseEntity = restClient.get()
                .uri(baseUrl + API_PREFIX + "/order-no/{orderNo}", orderNo)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderResponse>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.debug("调用查询用户订单列表接口，用户ID: {}", userId);

        ResponseEntity<ApiRestResponse<List<OrderResponse>>> responseEntity = restClient.get()
                .uri(baseUrl + API_PREFIX + "/user/{userId}", userId)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<List<OrderResponse>>>() {
                });

        return handleResponse(responseEntity.getBody());
    }

    @Override
    public OrderPageResponse queryOrders(OrderQueryRequest query) {
        log.debug("调用分页查询订单接口，查询条件: {}", query);

        if (!query.isValid()) {
            throw new IllegalArgumentException("查询参数无效");
        }

        ResponseEntity<ApiRestResponse<OrderPageResponse>> responseEntity = restClient.post()
                .uri(baseUrl + API_PREFIX + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(query)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiRestResponse<OrderPageResponse>>() {
                });

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

