package mr.demonid.service.catalog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Структура резервирования товаров
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blocked_products")
public class BlockedProduct {
    @Id
    private UUID orderId;

    private Long productId;
    private int quantity;
}
