package com.rolivhuwa.orders.order;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.rolivhuwa.orders.order.client.InventoryClient;
import com.rolivhuwa.orders.order.client.PaymentClient;
import com.rolivhuwa.orders.order.client.PaymentRequest;
import com.rolivhuwa.orders.order.client.PaymentResponse;
import com.rolivhuwa.orders.order.client.ReservationResponse;
import com.rolivhuwa.orders.order.client.ShipmentRequest;
import com.rolivhuwa.orders.order.client.ShipmentResponse;
import com.rolivhuwa.orders.order.client.ShippingClient;
import com.rolivhuwa.orders.order.client.StockRequest;

// The order flow: 1) reserve stock  2) take payment  3) create shipment
@Service
public class OrderService {

    private final OrderRepository orders;
    private final InventoryClient inventory;
    private final PaymentClient payments;
    private final ShippingClient shipping;

    public OrderService(OrderRepository orders, InventoryClient inventory,
                        PaymentClient payments, ShippingClient shipping) {
        this.orders = orders;
        this.inventory = inventory;
        this.payments = payments;
        this.shipping = shipping;
    }

    public CustomerOrder placeOrder(OrderRequest request) {
        BigDecimal total = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        CustomerOrder order = new CustomerOrder(request.sku(), request.quantity(), total, request.address());
        StockRequest stock = new StockRequest(request.sku(), request.quantity());

        // 1) Reserve stock. If inventory is down, the fallback stops the order with HTTP 503.
        ReservationResponse reservation = inventory.reserve(stock);
        if (!reservation.reserved()) {
            order.setStatus(OrderStatus.OUT_OF_STOCK);
            order.setNote(reservation.message());
            return orders.save(order);
        }

        // Save first so the order has an id for the payment
        order = orders.save(order);

        // 2) Take payment. If it fails for any reason, put the stock back.
        PaymentResponse payment;
        try {
            payment = payments.pay(new PaymentRequest(order.getId(), total));
        } catch (ServiceUnavailableException e) {
            inventory.release(stock);
            orders.delete(order);
            throw e;
        }
        if (!"APPROVED".equals(payment.status())) {
            inventory.release(stock);
            order.setStatus(OrderStatus.PAYMENT_DECLINED);
            order.setNote("Payment declined");
            return orders.save(order);
        }

        // 3) Create the shipment. If shipping is down, keep the order and ship later.
        ShipmentResponse shipment = shipping.create(new ShipmentRequest(order.getId(), request.address()));
        if (shipment == null) {
            order.setStatus(OrderStatus.CONFIRMED_SHIPPING_PENDING);
            order.setNote("Paid. Shipping service was down, shipment will be created later.");
        } else {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setTrackingNumber(shipment.trackingNumber());
        }
        return orders.save(order);
    }
}
