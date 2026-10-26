package com.rolivhuwa.orders.payment;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(@NotNull Long orderId, @NotNull @Positive BigDecimal amount) {
}
