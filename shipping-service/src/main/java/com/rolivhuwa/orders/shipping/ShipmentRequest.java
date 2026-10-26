package com.rolivhuwa.orders.shipping;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShipmentRequest(@NotNull Long orderId, @NotBlank String address) {
}
