package io.ddd4j.boot.sample.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

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

    public final static class CreateOrderRequest {

        private static final long serialVersionUID = 0L;

        @NotBlank
        private final String orderNo;

        @NotBlank
        private final String buyerId;

        @NotBlank
        private final String buyerName;

        @JsonCreator()
        public CreateOrderRequest(@NotBlank @JsonProperty("orderNo") String orderNo, @NotBlank @JsonProperty("buyerId") String buyerId, @NotBlank @JsonProperty("buyerName") String buyerName) {
            this.orderNo = orderNo;
            this.buyerId = buyerId;
            this.buyerName = buyerName;
        }

        @NotBlank
        @JsonProperty("orderNo")
        public String orderNo() {
            return orderNo;
        }

        @NotBlank
        @JsonProperty("buyerId")
        public String buyerId() {
            return buyerId;
        }

        @NotBlank
        @JsonProperty("buyerName")
        public String buyerName() {
            return buyerName;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (Objects.isNull(obj) || getClass() != obj.getClass()) {
                return false;
            }
            CreateOrderRequest other = (CreateOrderRequest) obj;
            return Objects.equals(this.orderNo, other.orderNo) && Objects.equals(this.buyerId, other.buyerId) && Objects.equals(this.buyerName, other.buyerName);
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + Objects.hashCode(orderNo);
            result = 31 * result + Objects.hashCode(buyerId);
            result = 31 * result + Objects.hashCode(buyerName);
            return result;
        }

        @Override
        public String toString() {
            return "CreateOrderRequest[orderNo=" + orderNo + ", buyerId=" + buyerId + ", buyerName=" + buyerName + "]";
        }
    }

    public final static class AddOrderLineRequest {

        private static final long serialVersionUID = 0L;

        @NotBlank
        private final String goodsId;

        @NotBlank
        private final String goodsName;

        @Min(1)
        private final int quantity;

        @DecimalMin(value = "0.01")
        private final BigDecimal unitPrice;

        @JsonCreator()
        public AddOrderLineRequest(@NotBlank @JsonProperty("goodsId") String goodsId, @NotBlank @JsonProperty("goodsName") String goodsName, @Min(1) @JsonProperty("quantity") int quantity, @DecimalMin(value = "0.01") @JsonProperty("unitPrice") BigDecimal unitPrice) {
            this.goodsId = goodsId;
            this.goodsName = goodsName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        @NotBlank
        @JsonProperty("goodsId")
        public String goodsId() {
            return goodsId;
        }

        @NotBlank
        @JsonProperty("goodsName")
        public String goodsName() {
            return goodsName;
        }

        @Min(1)
        @JsonProperty("quantity")
        public int quantity() {
            return quantity;
        }

        @DecimalMin(value = "0.01")
        @JsonProperty("unitPrice")
        public BigDecimal unitPrice() {
            return unitPrice;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (Objects.isNull(obj) || getClass() != obj.getClass()) {
                return false;
            }
            AddOrderLineRequest other = (AddOrderLineRequest) obj;
            return Objects.equals(this.goodsId, other.goodsId) && Objects.equals(this.goodsName, other.goodsName) && this.quantity == other.quantity && Objects.equals(this.unitPrice, other.unitPrice);
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + Objects.hashCode(goodsId);
            result = 31 * result + Objects.hashCode(goodsName);
            result = 31 * result + Integer.hashCode(quantity);
            result = 31 * result + Objects.hashCode(unitPrice);
            return result;
        }

        @Override
        public String toString() {
            return "AddOrderLineRequest[goodsId=" + goodsId + ", goodsName=" + goodsName + ", quantity=" + quantity + ", unitPrice=" + unitPrice + "]";
        }
    }
}
