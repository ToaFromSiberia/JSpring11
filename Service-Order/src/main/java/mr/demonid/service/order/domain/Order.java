package mr.demonid.service.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;
    private long userId;
    private long productId;
    private int quantity;
    private BigDecimal price;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order(long userId, long productId, int quantity, BigDecimal price, LocalDateTime orderDate, OrderStatus status) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return userId == order.userId && productId == order.productId && quantity == order.quantity && Objects.equals(orderId, order.orderId) && Objects.equals(price, order.price) && Objects.equals(orderDate, order.orderDate) && status == order.status;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(orderId);
        result = 31 * result + Long.hashCode(userId);
        result = 31 * result + Long.hashCode(productId);
        result = 31 * result + quantity;
        result = 31 * result + Objects.hashCode(price);
        result = 31 * result + Objects.hashCode(orderDate);
        result = 31 * result + Objects.hashCode(status);
        return result;
    }
}
