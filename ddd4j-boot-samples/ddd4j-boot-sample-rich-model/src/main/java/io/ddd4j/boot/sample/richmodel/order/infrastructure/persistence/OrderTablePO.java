package io.ddd4j.boot.sample.richmodel.order.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * MyBatis persistence object for sample_order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sample_order")
public class OrderTablePO {

    @TableId
    private String id;
    private String orderNo;
    private String buyerId;
    private String buyerName;
    private String status;
    private BigDecimal totalAmount;
    private String currency;
}
