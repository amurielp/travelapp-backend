package com.travelapp.shared.valueobjects;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) throw new IllegalArgumentException("Currency mismatch");
        return new Money(this.amount.add(other.amount), currency);
    }
    public static Money of(double amount, String currency) { return new Money(BigDecimal.valueOf(amount), currency); }
    public static Money zero(String currency) { return new Money(BigDecimal.ZERO, currency); }
}
