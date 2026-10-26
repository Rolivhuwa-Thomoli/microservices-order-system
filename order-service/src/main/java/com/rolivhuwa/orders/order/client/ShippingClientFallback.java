package com.rolivhuwa.orders.order.client;

import org.springframework.stereotype.Component;

// Shipping can wait: the order is already paid, so we do not cancel it.
// We return "no shipment yet" and the order is marked to ship later.
@Component
public class ShippingClientFallback implements ShippingClient {

    @Override
    public ShipmentResponse create(ShipmentRequest request) {
        return null;
    }
}
