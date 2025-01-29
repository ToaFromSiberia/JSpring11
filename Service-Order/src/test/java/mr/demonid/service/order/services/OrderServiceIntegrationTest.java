package mr.demonid.service.order.services;

import feign.FeignException;
import mr.demonid.service.order.domain.Order;
import mr.demonid.service.order.domain.OrderStatus;
import mr.demonid.service.order.dto.PaymentRequest;
import mr.demonid.service.order.dto.ProductReservationRequest;
import mr.demonid.service.order.exceptions.OrderThrowedException;
import mr.demonid.service.order.links.CatalogServiceClient;
import mr.demonid.service.order.links.PaymentServiceClient;
import mr.demonid.service.order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private CatalogServiceClient catalogServiceClient;

    @MockBean
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private OrderService orderService;      // тестируем сервис по созданию и проведению заказов.


    UUID orderId;
    long userId;
    long shopId;
    long productId;
    int quantity;
    BigDecimal price;
    /**
     * Подготавливаем данные для каждого тестового метода.
     */
    @BeforeEach
    public void setup() {
        userId = 1L;
        shopId = 2L;
        productId = 5L;
        quantity = 1;
        price = BigDecimal.valueOf(800);

        orderRepository.deleteAll(); // Очищаем БД перед каждым тестом
    }


    /**
     * Проверяем поведение при корректных входных данных.
     */
    @Test
    void createOrder_Success() {
        /*
            Подготовка.
         */
        when(catalogServiceClient.reserve(any(ProductReservationRequest.class))).thenReturn(ResponseEntity.ok().build());
        when(paymentServiceClient.transfer(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok().build());
        when(catalogServiceClient.approve(orderId)).thenReturn(ResponseEntity.ok().build());
        /*
            Выполняем.
         */
        UUID res = assertDoesNotThrow( () -> orderService.createOrder(userId, shopId, productId, quantity, price));
        /*
            Проверяем возвращаемый результат.
         */
        Assertions.assertNotNull(res);
        Optional<Order> order = orderRepository.findById(res);                  // запрашиваем заказ из БД
        Assertions.assertTrue(order.isPresent());                               // убеждаемся что он есть
        Assertions.assertEquals(order.get().getStatus(), OrderStatus.Approved); // статус должен быть Approved.
        // проверяем вызовы методов и внешних сервисов
        verify(catalogServiceClient, times(1)).reserve(any(ProductReservationRequest.class));
        verify(paymentServiceClient, times(1)).transfer(any(PaymentRequest.class));
        verify(catalogServiceClient, times(1)).approve(res);
    }

    /**
     * Проверяем поведение при ошибке резервации товара.
     */
    @Test
    void createOrder_FailureCatalogReservationTest() {
        /*
            Подготовка.
         */
        doThrow(FeignException.FeignClientException.class).when(catalogServiceClient).reserve(any(ProductReservationRequest.class));
        /*
            Выполняем.
         */
        assertThrows(OrderThrowedException.class, () -> orderService.createOrder(userId, shopId, productId, quantity, price));
        /*
            Проверяем, что заказ был отменен
         */
        List<Order> orders = orderRepository.findAll();
        assertFalse(orders.isEmpty());
        assertEquals(orders.get(0).getStatus(), OrderStatus.Cancelled);
        // проверяем вызовы методов
        verify(catalogServiceClient, times(1)).reserve(any(ProductReservationRequest.class));
        verify(catalogServiceClient, times(1)).unblock(orders.get(0).getOrderId());
        verifyNoInteractions(paymentServiceClient);
    }

    /**
     * Проверяем поведение при неудачном транзите средств.
     */
    @Test
    void createOrder_FailurePaymentTransferTest() {
        /*
            Подготовка.
         */
        when(catalogServiceClient.reserve(any(ProductReservationRequest.class))).thenReturn(ResponseEntity.ok().build());
        doThrow(FeignException.FeignClientException.class).when(paymentServiceClient).transfer(any(PaymentRequest.class));
        /*
            Выполняем.
         */
        assertThrows(OrderThrowedException.class, () -> orderService.createOrder(userId, shopId, productId, quantity, price));
        /*
            Проверяем.
         */
        List<Order> orders = orderRepository.findAll();
        assertFalse(orders.isEmpty());
        assertEquals(orders.get(0).getStatus(), OrderStatus.Cancelled);     // статус заказа должен быть "Отменён"
        verify(catalogServiceClient, times(1)).reserve(any(ProductReservationRequest.class));
        verify(paymentServiceClient, times(1)).transfer(any(PaymentRequest.class));
        verify(catalogServiceClient, times(1)).unblock(orders.get(0).getOrderId());
    }

}
