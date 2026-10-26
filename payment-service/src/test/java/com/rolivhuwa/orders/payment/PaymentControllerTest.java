package com.rolivhuwa.orders.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "eureka.client.enabled=false")
class PaymentControllerTest {

    @Autowired
    PaymentController controller;

    @Test
    void approvesNormalPayment() {
        assertThat(controller.pay(new PaymentRequest(1L, new BigDecimal("999.00"))).getStatus())
                .isEqualTo("APPROVED");
    }

    @Test
    void declinesPaymentAboveLimit() {
        assertThat(controller.pay(new PaymentRequest(2L, new BigDecimal("75000.00"))).getStatus())
                .isEqualTo("DECLINED");
    }
}
