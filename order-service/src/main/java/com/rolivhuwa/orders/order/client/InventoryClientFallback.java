package com.rolivhuwa.orders.order.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rolivhuwa.orders.order.ServiceUnavailableException;

// Used when the inventory service is down (the circuit breaker is open)
@Component
public class InventoryClientFallback implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClientFallback.class);

    @Override
    public ReservationResponse reserve(StockRequest request) {
        // We cannot sell stock we cannot check, so stop the order here
        throw new ServiceUnavailableException("Inventory service is unavailable. Please try again later.");
    }

    @Override
    public void release(StockRequest request) {
        // Write it down so someone can put the stock back later
        log.error("Could not release {} x {}: inventory service unavailable", request.quantity(), request.sku());
    }
}
