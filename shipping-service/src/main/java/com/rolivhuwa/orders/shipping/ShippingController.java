package com.rolivhuwa.orders.shipping;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments")
public class ShippingController {

    private final ShipmentRepository shipments;

    public ShippingController(ShipmentRepository shipments) {
        this.shipments = shipments;
    }

    @PostMapping
    public Shipment create(@Valid @RequestBody ShipmentRequest request) {
        String tracking = "MZ" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return shipments.save(new Shipment(request.orderId(), request.address(), tracking, "CREATED"));
    }
}
