package com.rolivhuwa.orders.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shipping-service", fallback = ShippingClientFallback.class)
public interface ShippingClient {

    @PostMapping("/api/shipments")
    ShipmentResponse create(@RequestBody ShipmentRequest request);
}
