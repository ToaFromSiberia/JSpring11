package mr.demonid.service.order.services;

import feign.FeignException;
import mr.demonid.service.order.domain.Order;
import mr.demonid.service.order.dto.PaymentRequest;
import mr.demonid.service.order.dto.ProductReservationRequest;
import mr.demonid.service.order.exceptions.BadOrderException;
import mr.demonid.service.order.exceptions.OrderThrowedException;
import mr.demonid.service.order.links.CatalogServiceClient;
import mr.demonid.service.order.links.PaymentServiceClient;
import mr.demonid.service.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты.
 */

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CatalogServiceClient catalogServiceClient;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
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
        orderId = UUID.randomUUID();
        userId = 1L;
        shopId = 2L;
        productId = 5L;
        quantity = 1;
        price = BigDecimal.valueOf(800);
    }

    /**
     * Проверяем поведение при корректных входных данных.
     */
    @Test
    void createOrder_SuccessfulTest() {
        /*
            Подготовка.
         */
        when(catalogServiceClient.reserve(any(ProductReservationRequest.class))).thenReturn(ResponseEntity.ok().build());
        when(paymentServiceClient.transfer(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok().build());
        when(catalogServiceClient.approve(orderId)).thenReturn(ResponseEntity.ok().build());
        // имитируем поведение при записи в БД - инициализируем Id объекта.
        when(orderRepository.save(any(Order.class))).thenAnswer(e -> {
            Order order = e.getArgument(0);
            order.setOrderId(orderId);
            return order;
        });
        /*
            Выполняем.
         */
        UUID res = assertDoesNotThrow( () -> orderService.createOrder(userId, shopId, productId, quantity, price));
        /*
            Проверяем возвращаемый результат.
         */
        assertEquals(orderId, res);
        verify(catalogServiceClient).approve(orderId);
        verify(orderRepository, times(2)).save(any(Order.class));   // save() должно было вызвано 2 раза
    }

    /**
     * Проверяем поведение при ошибке БД на запись объекта.
     */
    @Test
    void createOrder_BadOrderExceptionText() {
        /*
            Подготовка.
         */
        when(orderRepository.save(any(Order.class))).thenAnswer(e -> {
            Order order = e.getArgument(0);
            order.setOrderId(null);                 // имитируем ошибку записи в БД - возвращаем пустой Id.
            return order;
        });
        /*
            Выполняем и проверяем.
            Должно возникнуть исключение BadOrderException().
         */
        assertThrows(BadOrderException.class, () -> orderService.createOrder(userId, shopId, productId, quantity, price));
        /*
            Проверяем, что методы этих объектов не вызывались.
         */
        verifyNoInteractions(catalogServiceClient);
        verifyNoInteractions(paymentServiceClient);
    }

    /**
     * Проверяем поведение при ошибке резервирования товара.
     */
    @Test
    void createOrder_ReserveException() {
        /*
            Подготовка.
         */
        when(orderRepository.save(any(Order.class))).thenAnswer(e -> {
            Order order = e.getArgument(0);
            order.setOrderId(orderId);              // имитируем поведение при записи в БД - инициализируем Id объекта.
            return order;
        });
        // имитируем возникновение FeignException при выполнении reserve()
        doThrow(FeignException.FeignClientException.class).when(catalogServiceClient).reserve(any(ProductReservationRequest.class));
        /*
            Выполняем.
         */
        assertThrows(OrderThrowedException.class, () -> orderService.createOrder(userId, shopId, productId, quantity, price));

        /*
            Проверяем. В том числе действия в блоке catch.
         */
        verifyNoInteractions(paymentServiceClient);
        verify(catalogServiceClient).unblock(orderId);
        verify(orderRepository, times(2)).save(any(Order.class));   // save() должно было вызвано 2 раза
    }

    /**
     * Проверяем поведение при ошибке непосредственно трансфера.
     */
    @Test
    void createOrder_TransferExcept() {
        /*
            Подготовка.
         */
        when(catalogServiceClient.reserve(any(ProductReservationRequest.class))).thenReturn(ResponseEntity.ok().build());
        doThrow(FeignException.FeignClientException.class).when(paymentServiceClient).transfer(any(PaymentRequest.class));
        when(orderRepository.save(any(Order.class))).thenAnswer(e -> {
            Order order = e.getArgument(0);
            order.setOrderId(orderId);              // имитируем поведение при записи в БД - инициализируем Id объекта.
            return order;
        });
        /*
            Выполняем.
         */
        assertThrows(OrderThrowedException.class, () -> orderService.createOrder(userId, shopId, productId, quantity, price));
        /*
            Проверяем. В том числе действия в блоке catch.
         */
        verify(catalogServiceClient).reserve(any(ProductReservationRequest.class));
        verify(catalogServiceClient).unblock(orderId);
        verify(orderRepository, times(2)).save(any(Order.class));   // save() должно было вызвано 2 раза
    }

}
