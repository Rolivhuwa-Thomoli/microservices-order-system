package com.rolivhuwa.orders.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StockRequest(@NotBlank String sku, @Min(1) int quantity) {
}
