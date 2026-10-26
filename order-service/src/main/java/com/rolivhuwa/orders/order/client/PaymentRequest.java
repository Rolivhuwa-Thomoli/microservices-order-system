package com.rolivhuwa.orders.order.client;

import java.math.BigDecimal;

public record PaymentRequest(Long orderId, BigDecimal amount) {
}
