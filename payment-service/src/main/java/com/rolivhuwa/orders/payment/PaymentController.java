package com.rolivhuwa.orders.payment;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository payments;
    private final BigDecimal maxAmount;

    public PaymentController(PaymentRepository payments, @Value("${payment.max-amount}") BigDecimal maxAmount) {
        this.payments = payments;
        this.maxAmount = maxAmount;
    }

    // A pretend card payment: approve it unless it is above the limit
    @PostMapping
    public Payment pay(@Valid @RequestBody PaymentRequest request) {
        String status = request.amount().compareTo(maxAmount) > 0 ? "DECLINED" : "APPROVED";
        return payments.save(new Payment(request.orderId(), request.amount(), status));
    }
}
