package io.ddd4j.boot.sample.order.domain.model.vo;

import java.math.BigDecimal;

/**
 * 金额值对象
 *
 * @param amount 金额
 * @param currency 币种
 *
 */
public record Money(BigDecimal amount, String currency) {

    /**
     * 构造金额值对象时校验金额与币种合法。
     */
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("货币类型不能为空");
        }
    }

    /**
     * 金额相加（要求币种一致）。
     *
     * @param other 相加的金额
     * @return 相加后的金额
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同货币类型不能相加");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 金额相减（要求币种一致）。
     *
     * @param other 相减的金额
     * @return 相减后的金额
     */
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同货币类型不能相减");
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * 金额乘以倍数。
     *
     * @param multiplier 乘数
     * @return 乘算后的金额
     */
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currency);
    }

}
