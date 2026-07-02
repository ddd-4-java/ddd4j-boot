package io.ddd4j.boot.sample.richmodel.order.web;

import io.ddd4j.sample.richmodel.order.application.AddOrderLineCommand;
import io.ddd4j.sample.richmodel.order.application.CreateOrderCommand;
import io.ddd4j.sample.richmodel.order.application.OrderApplicationService;
import io.ddd4j.sample.richmodel.order.domain.model.Order;
import io.ddd4j.sample.richmodel.order.domain.repository.OrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * WebMVC adapter for the rich-model sample.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService applicationService;
    private final OrderRepository repository;

    public OrderController(OrderApplicationService applicationService, OrderRepository repository) {
        this.applicationService = Objects.requireNonNull(applicationService, "applicationService must not be null");
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @PostMapping
    public OrderResponse create(@RequestBody CreateOrderRequest request) {
        Order order = applicationService.createDraft(new CreateOrderCommand(
                request.orderNo(),
                request.buyerId(),
                request.buyerName()
        ));
        return OrderResponse.from(order);
    }

    @PostMapping("/{orderId}/lines")
    public OrderResponse addLine(@PathVariable String orderId, @RequestBody AddLineRequest request) {
        Order order = applicationService.addLine(new AddOrderLineCommand(
                orderId,
                request.productId(),
                request.productName(),
                request.quantity(),
                request.unitPrice()
        ));
        return OrderResponse.from(order);
    }

    @PostMapping("/{orderId}/pay")
    public OrderResponse pay(@PathVariable String orderId) {
        return OrderResponse.from(applicationService.pay(orderId));
    }

    @GetMapping("/by-no/{orderNo}")
    public OrderResponse findByOrderNo(@PathVariable String orderNo) {
        return repository.findByOrderNo(orderNo)
                .map(OrderResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderNo));
    }

    public record CreateOrderRequest(String orderNo, String buyerId, String buyerName) {
    }

    public record AddLineRequest(String productId, String productName, int quantity, BigDecimal unitPrice) {
    }

    public record OrderResponse(
            String id,
            String orderNo,
            String buyerId,
            String buyerName,
            String status,
            BigDecimal totalAmount,
            List<OrderLineResponse> lines
    ) {

        static OrderResponse from(Order order) {
            return new OrderResponse(
                    order.id(),
                    order.orderNo(),
                    order.buyerId(),
                    order.buyerName(),
                    order.status().name(),
                    order.totalAmount().amount(),
                    order.lines().stream().map(OrderLineResponse::from).toList()
            );
        }
    }

    public record OrderLineResponse(String id, String productId, String productName, int quantity, BigDecimal subtotal) {

        static OrderLineResponse from(io.ddd4j.sample.richmodel.order.domain.model.OrderLine line) {
            return new OrderLineResponse(
                    line.id(),
                    line.productId(),
                    line.productName(),
                    line.quantity(),
                    line.subtotal().amount()
            );
        }
    }
}
