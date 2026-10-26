package com.rolivhuwa.orders.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.rolivhuwa.orders.order.client.InventoryClient;
import com.rolivhuwa.orders.order.client.PaymentClient;
import com.rolivhuwa.orders.order.client.PaymentResponse;
import com.rolivhuwa.orders.order.client.ReservationResponse;
import com.rolivhuwa.orders.order.client.ShipmentResponse;
import com.rolivhuwa.orders.order.client.ShippingClient;
import com.rolivhuwa.orders.order.client.StockRequest;

// Tests the order flow with fake (mock) inventory, payment and shipping services
@SpringBootTest(properties = "eureka.client.enabled=false")
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @MockitoBean
    InventoryClient inventory;

    @MockitoBean
    PaymentClient payments;

    @MockitoBean
    ShippingClient shipping;

    private final OrderRequest request =
            new OrderRequest("PHONE-01", 2, new BigDecimal("4500.00"), "12 Main Road, Thohoyandou");

    @Test
    void happyPathConfirmsOrderWithTrackingNumber() {
        when(inventory.reserve(any())).thenReturn(new ReservationResponse(true, "ok"));
        when(payments.pay(any())).thenReturn(new PaymentResponse(1L, "APPROVED"));
        when(shipping.create(any())).thenReturn(new ShipmentResponse(1L, "MZ1234", "CREATED"));

        CustomerOrder order = orderService.placeOrder(request);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("9000.00");
        assertThat(order.getTrackingNumber()).isEqualTo("MZ1234");
    }

    @Test
    void outOfStockStopsBeforePayment() {
        when(inventory.reserve(any())).thenReturn(new ReservationResponse(false, "Not enough stock"));

        CustomerOrder order = orderService.placeOrder(request);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.OUT_OF_STOCK);
        verify(payments, never()).pay(any());
    }

    @Test
    void declinedPaymentPutsStockBack() {
        when(inventory.reserve(any())).thenReturn(new ReservationResponse(true, "ok"));
        when(payments.pay(any())).thenReturn(new PaymentResponse(1L, "DECLINED"));

        CustomerOrder order = orderService.placeOrder(request);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_DECLINED);
        verify(inventory).release(new StockRequest("PHONE-01", 2));
    }

    @Test
    void paymentServiceDownPutsStockBackAndReturns503() {
        when(inventory.reserve(any())).thenReturn(new ReservationResponse(true, "ok"));
        when(payments.pay(any())).thenThrow(new ServiceUnavailableException("down"));

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(ServiceUnavailableException.class);
        verify(inventory).release(new StockRequest("PHONE-01", 2));
    }

    @Test
    void shippingDownStillKeepsThePaidOrder() {
        when(inventory.reserve(any())).thenReturn(new ReservationResponse(true, "ok"));
        when(payments.pay(any())).thenReturn(new PaymentResponse(1L, "APPROVED"));
        when(shipping.create(any())).thenReturn(null);

        CustomerOrder order = orderService.placeOrder(request);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED_SHIPPING_PENDING);
    }
}
