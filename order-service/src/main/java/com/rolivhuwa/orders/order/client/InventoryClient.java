package com.rolivhuwa.orders.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Feign writes the HTTP code for us. "inventory-service" is looked up in Eureka.
@FeignClient(name = "inventory-service", fallback = InventoryClientFallback.class)
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    ReservationResponse reserve(@RequestBody StockRequest request);

    @PostMapping("/api/inventory/release")
    void release(@RequestBody StockRequest request);
}
