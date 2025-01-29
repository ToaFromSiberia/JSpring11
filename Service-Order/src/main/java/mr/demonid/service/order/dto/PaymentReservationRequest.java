package mr.demonid.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentReservationRequest {
    private UUID orderId;
    private long userId;
    private long productId;
    private BigDecimal amount;
}
