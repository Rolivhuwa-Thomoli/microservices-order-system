package com.rolivhuwa.orders.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "eureka.client.enabled=false")
class InventoryServiceTest {

    @Autowired
    InventoryService inventory;

    @Autowired
    ProductRepository products;

    @Test
    void reservesStockWhenThereIsEnough() {
        ReservationResponse r = inventory.reserve(new StockRequest("HEADSET-01", 2));
        assertThat(r.reserved()).isTrue();
        assertThat(products.findById("HEADSET-01").orElseThrow().getQuantity()).isEqualTo(48);
    }

    @Test
    void refusesWhenStockIsTooLow() {
        ReservationResponse r = inventory.reserve(new StockRequest("LAPTOP-01", 999));
        assertThat(r.reserved()).isFalse();
        assertThat(products.findById("LAPTOP-01").orElseThrow().getQuantity()).isEqualTo(10);
    }
}
