package mr.demonid.service.order.services;

import feign.FeignException;
import lombok.AllArgsConstructor;
import mr.demonid.service.order.domain.Order;
import mr.demonid.service.order.domain.OrderStatus;
import mr.demonid.service.order.dto.PaymentRequest;
import mr.demonid.service.order.dto.ProductReservationRequest;
import mr.demonid.service.order.exceptions.BadOrderException;
import mr.demonid.service.order.exceptions.OrderThrowedException;
import mr.demonid.service.order.links.CatalogServiceClient;
import mr.demonid.service.order.links.PaymentServiceClient;
import mr.demonid.service.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;

    private CatalogServiceClient catalogServiceClient;
    private PaymentServiceClient paymentServiceClient;

    /**
     * Создаёт новый заказ и проводит его через все этапы.
     * @param userId    Заказчик.
     * @param shopId    Магазин.
     * @param productId Код товара.
     * @param quantity  Количество.
     * @param price     Стоимость за единицу.
     * @return Идентификатор заказа, или NULL в случае неудачи (например отказ БД).
     */
    public UUID createOrder(long userId, long shopId, long productId, int quantity, BigDecimal price) {
        // Создаём заказ
        Order order = new Order(userId, productId, quantity, price, LocalDateTime.now(), OrderStatus.Pending);
        order = orderRepository.save(order);
        if (order.getOrderId() == null) {
            throw new BadOrderException();      // ошибка создания заказа, возможно БД недоступна
        }
        // Заказ создан, сопровождаем его до завершения, либо до отмены.
        ProductReservationRequest request = new ProductReservationRequest(order.getOrderId(), userId, shopId, productId, quantity, price);
        try {
            // Резервируем товар
            catalogServiceClient.reserve(request);
            // Резервируем средства на счету пользователя
            PaymentRequest paymentRequest = new PaymentRequest(order.getOrderId(), userId, shopId, price.multiply(BigDecimal.valueOf(quantity)), "BUY");
            paymentServiceClient.transfer(paymentRequest);
            // Меняем статус заказа
            order.setStatus(OrderStatus.Approved);
            orderRepository.save(order);
            // и подтверждаем списание резерва
            catalogServiceClient.approve(order.getOrderId());
            return order.getOrderId();

        } catch (FeignException e) {
            System.out.println(e.contentUTF8());
            // отменяем заказ
            order.setStatus(OrderStatus.Cancelled);
            orderRepository.save(order);
            // отменяем резерв товара
            catalogServiceClient.unblock(order.getOrderId());
            // переправляем ошибку дальше
            throw new OrderThrowedException(e.contentUTF8());
        }
    }

    /**
     * Возвращает список всех находящихся в обработке заказов.
     */
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

}
