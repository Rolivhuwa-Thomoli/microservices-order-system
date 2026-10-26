package com.rolivhuwa.orders.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final ProductRepository products;

    public InventoryService(ProductRepository products) {
        this.products = products;
    }

    // Take stock out for an order, but only if there is enough
    @Transactional
    public ReservationResponse reserve(StockRequest request) {
        return products.findById(request.sku())
                .map(p -> {
                    if (p.getQuantity() < request.quantity()) {
                        return new ReservationResponse(false, "Not enough stock for " + p.getSku());
                    }
                    p.setQuantity(p.getQuantity() - request.quantity());
                    return new ReservationResponse(true, "Reserved " + request.quantity() + " x " + p.getSku());
                })
                .orElse(new ReservationResponse(false, "Unknown product " + request.sku()));
    }

    // Put stock back, for example when a payment fails
    @Transactional
    public void release(StockRequest request) {
        products.findById(request.sku())
                .ifPresent(p -> p.setQuantity(p.getQuantity() + request.quantity()));
    }
}
