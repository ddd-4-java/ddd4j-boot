package io.ddd4j.boot.sample.domain.order.model.vo;

import java.math.BigDecimal;

/**
 * 金额值对象
 *
 * <p>领域语义：以不可变 record 封装「金额 + 币种」，杜绝裸 {@link BigDecimal}
 * 在各层流转时丢失币种信息；金额不允许为负，四则运算仅在同币种间进行，
 * 跨币种运算直接失败，避免静默产生错误金额。</p>
 *
 * @param amount   金额，不能为负数
 * @param currency 货币类型（如 CNY），不能为空
 */
public record Money(BigDecimal amount, String currency) {

    /**
     * 紧凑构造器，校验金额与币种。
     *
     * @throws IllegalArgumentException 金额为空或为负、货币类型为空或纯空白时抛出
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
     * 同币种金额相加。
     *
     * @param other 相加的另一笔金额，币种必须与当前金额一致
     * @return 相加后的新金额对象（保持当前币种）
     * @throws IllegalArgumentException 币种不一致或 {@code other} 为空时抛出
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同货币类型不能相加");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 同币种金额相减。
     *
     * @param other 被减的金额，币种必须与当前金额一致
     * @return 相减后的新金额对象（保持当前币种）
     * @throws IllegalArgumentException 币种不一致或 {@code other} 为空时抛出
     */
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同货币类型不能相减");
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * 金额乘以倍数（如单价 × 数量）。
     *
     * @param multiplier 乘数
     * @return 乘算后的新金额对象（保持当前币种）
     * @throws IllegalArgumentException 乘算结果为负时抛出（受值对象不变式约束）
     */
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currency);
    }

}
