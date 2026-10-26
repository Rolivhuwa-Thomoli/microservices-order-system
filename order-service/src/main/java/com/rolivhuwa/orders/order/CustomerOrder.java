package com.rolivhuwa.orders.order;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

// "Order" is a reserved word in SQL, so the table is called customer_order
@Entity
public class CustomerOrder {

    @Id
    @GeneratedValue
    private Long id;
    private String sku;
    private int quantity;
    private BigDecimal totalAmount;
    private String address;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String trackingNumber;
    private String note;
    private Instant createdAt = Instant.now();

    protected CustomerOrder() {
    }

    public CustomerOrder(String sku, int quantity, BigDecimal totalAmount, String address) {
        this.sku = sku;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getAddress() { return address; }
    public OrderStatus getStatus() { return status; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getNote() { return note; }
    public Instant getCreatedAt() { return createdAt; }

    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setNote(String note) { this.note = note; }
}
