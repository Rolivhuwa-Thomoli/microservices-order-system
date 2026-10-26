package com.rolivhuwa.orders.order;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
        @NotBlank String sku,
        @Min(1) int quantity,
        @NotNull @Positive BigDecimal unitPrice,
        @NotBlank String address) {
}
