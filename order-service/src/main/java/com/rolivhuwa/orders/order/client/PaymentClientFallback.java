package com.rolivhuwa.orders.order.client;

import org.springframework.stereotype.Component;

import com.rolivhuwa.orders.order.ServiceUnavailableException;

@Component
public class PaymentClientFallback implements PaymentClient {

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        throw new ServiceUnavailableException("Payment service is unavailable. Please try again later.");
    }
}
