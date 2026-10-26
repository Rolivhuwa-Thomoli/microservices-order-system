package com.rolivhuwa.orders.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Puts a few products in the warehouse when the service starts (only if it is empty)
@Component
public class SampleData implements CommandLineRunner {

    private final ProductRepository products;

    public SampleData(ProductRepository products) {
        this.products = products;
    }

    @Override
    public void run(String... args) {
        if (products.count() == 0) {
            products.save(new Product("LAPTOP-01", "Laptop 15 inch", 10));
            products.save(new Product("PHONE-01", "Smartphone", 25));
            products.save(new Product("HEADSET-01", "Wireless headset", 50));
        }
    }
}
