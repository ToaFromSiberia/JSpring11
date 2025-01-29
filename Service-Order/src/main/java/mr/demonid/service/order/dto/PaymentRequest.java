package mr.demonid.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Запрос на перевод средств с одного счета на другой.
 */
@Data
@AllArgsConstructor
public class PaymentRequest {
    private UUID orderId;
    private Long fromUserId;
    private Long recipientId;
    private BigDecimal transferAmount;
    private String type;            // "DEBIT", "CREDIT" и тд.
}
