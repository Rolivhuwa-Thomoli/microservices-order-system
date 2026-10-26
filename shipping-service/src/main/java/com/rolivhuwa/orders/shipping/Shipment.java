package com.rolivhuwa.orders.shipping;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Shipment {

    @Id
    @GeneratedValue
    private Long id;
    private Long orderId;
    private String address;
    private String trackingNumber;
    private String status;   // CREATED

    protected Shipment() {
    }

    public Shipment(Long orderId, String address, String trackingNumber, String status) {
        this.orderId = orderId;
        this.address = address;
        this.trackingNumber = trackingNumber;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getAddress() { return address; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getStatus() { return status; }
}
