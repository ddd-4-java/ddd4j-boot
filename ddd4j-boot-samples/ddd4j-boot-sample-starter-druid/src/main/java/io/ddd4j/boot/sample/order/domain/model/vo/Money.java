package io.ddd4j.boot.sample.order.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

import java.math.BigDecimal;

/**
 * 金额值对象
 */
public final class Money {

    private static final long serialVersionUID = 0L;

    private final BigDecimal amount;

    private final String currency;

    @JsonCreator()
    public Money(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currency") String currency) {
        if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        if (Objects.isNull(currency) || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("货币类型不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同货币类型不能相加");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同货币类型不能相减");
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currency);
    }

    @JsonProperty("amount")
    public BigDecimal amount() {
        return amount;
    }

    @JsonProperty("currency")
    public String currency() {
        return currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (Objects.isNull(obj) || getClass() != obj.getClass()) {
            return false;
        }
        Money other = (Money) obj;
        return Objects.equals(this.amount, other.amount) && Objects.equals(this.currency, other.currency);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + Objects.hashCode(amount);
        result = 31 * result + Objects.hashCode(currency);
        return result;
    }

    @Override
    public String toString() {
        return "Money[amount=" + amount + ", currency=" + currency + "]";
    }
}

