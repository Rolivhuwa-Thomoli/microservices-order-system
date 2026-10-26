package com.rolivhuwa.orders.inventory;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventory;
    private final ProductRepository products;

    public InventoryController(InventoryService inventory, ProductRepository products) {
        this.inventory = inventory;
        this.products = products;
    }

    @GetMapping
    public List<Product> all() {
        return products.findAll();
    }

    @PostMapping("/reserve")
    public ReservationResponse reserve(@Valid @RequestBody StockRequest request) {
        return inventory.reserve(request);
    }

    @PostMapping("/release")
    public void release(@Valid @RequestBody StockRequest request) {
        inventory.release(request);
    }
}
