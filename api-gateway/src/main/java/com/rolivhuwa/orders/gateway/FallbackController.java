package com.rolivhuwa.orders.gateway;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Called by the circuit breaker when the order service is down or too slow.
// The client gets a clear message instead of a long wait or a crash.
@RestController
public class FallbackController {

    @RequestMapping("/fallback/orders")
    public ResponseEntity<Map<String, String>> ordersFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", "UNAVAILABLE",
                "message", "The order service is busy or down. Please try again in a moment."));
    }
}
