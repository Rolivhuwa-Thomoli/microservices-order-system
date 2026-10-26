package com.rolivhuwa.orders.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "eureka.client.enabled=false")
class ShippingControllerTest {

    @Autowired
    ShippingController controller;

    @Test
    void createsShipmentWithTrackingNumber() {
        Shipment s = controller.create(new ShipmentRequest(7L, "12 Main Road, Thohoyandou"));
        assertThat(s.getStatus()).isEqualTo("CREATED");
        assertThat(s.getTrackingNumber()).startsWith("MZ");
    }
}
