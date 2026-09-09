package io.ddd4j.boot.sample.order;

import io.ddd4j.core.api.R;
import io.ddd4j.sample.order.application.AddOrderLineCommand;
import io.ddd4j.sample.order.application.CreateOrderCommand;
import io.ddd4j.sample.order.application.OrderApplicationService;
import io.ddd4j.sample.order.application.OrderReadModel;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 显式订单用例路由，不使用动态 {@code /{model}} MVC 入口。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService applicationService;

    public OrderController(OrderApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public R<OrderReadModel> create(@Valid @RequestBody CreateOrderRequest request) {
        return R.ok(applicationService.find(applicationService.create(
                new CreateOrderCommand(request.orderNo(), request.buyerId(), request.buyerName())).id()));
    }

    @PostMapping("/{orderId}/lines")
    public R<OrderReadModel> addLine(@PathVariable String orderId, @Valid @RequestBody AddOrderLineRequest request) {
        applicationService.addLine(new AddOrderLineCommand(orderId, request.goodsId(), request.goodsName(),
                request.quantity(), request.unitPrice()));
        return R.ok(applicationService.find(orderId));
    }

    @PostMapping("/{orderId}/pay")
    public R<OrderReadModel> pay(@PathVariable String orderId,
                                 @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        String key = StringUtils.hasText(idempotencyKey) ? idempotencyKey : UUID.randomUUID().toString();
        applicationService.pay(orderId, key);
        return R.ok(applicationService.find(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public R<OrderReadModel> cancel(@PathVariable String orderId) {
        applicationService.cancel(orderId);
        return R.ok(applicationService.find(orderId));
    }

    @GetMapping("/{orderId}")
    public R<OrderReadModel> find(@PathVariable String orderId) {
        return R.ok(applicationService.find(orderId));
    }

    public record CreateOrderRequest(@NotBlank String orderNo, @NotBlank String buyerId, @NotBlank String buyerName) {
    }

    public record AddOrderLineRequest(@NotBlank String goodsId, @NotBlank String goodsName,
                                      @Min(1) int quantity, @DecimalMin(value = "0.01") BigDecimal unitPrice) {
    }
}
