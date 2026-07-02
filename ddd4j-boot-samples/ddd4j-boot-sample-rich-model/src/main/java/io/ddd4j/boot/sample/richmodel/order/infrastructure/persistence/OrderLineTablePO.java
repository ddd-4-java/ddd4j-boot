package io.ddd4j.boot.sample.richmodel.order.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * MyBatis persistence object for sample_order_line.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sample_order_line")
public class OrderLineTablePO {

    @TableId
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String currency;
}
