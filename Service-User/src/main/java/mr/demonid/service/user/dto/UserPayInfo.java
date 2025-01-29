package mr.demonid.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Ответ на запрос реквизитов пользователя от PaymentService
 */
@Data
@AllArgsConstructor
public class UserPayInfo {
    private long userId;
    private long accountId;
    private BigDecimal balance;
}
