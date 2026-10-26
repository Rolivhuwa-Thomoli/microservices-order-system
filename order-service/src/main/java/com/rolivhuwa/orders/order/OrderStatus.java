package com.rolivhuwa.orders.order;

public enum OrderStatus {
    CONFIRMED,          // stock reserved, paid, shipment created
    CONFIRMED_SHIPPING_PENDING, // paid, but shipping service was down: ship later
    OUT_OF_STOCK,       // not enough stock
    PAYMENT_DECLINED    // payment refused, stock put back
}
