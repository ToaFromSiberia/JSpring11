package mr.demonid.web.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Запрос на покупку товара
 */
@Data
@AllArgsConstructor
public class OrderRequest {
    private long productId;
    private int quantity;
    private BigDecimal price;
}
