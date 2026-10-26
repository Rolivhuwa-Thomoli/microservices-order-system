package com.rolivhuwa.orders.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// One product in the warehouse and how many are in stock
@Entity
public class Product {

    @Id
    private String sku;
    private String name;
    private int quantity;

    protected Product() {
    }

    public Product(String sku, String name, int quantity) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
