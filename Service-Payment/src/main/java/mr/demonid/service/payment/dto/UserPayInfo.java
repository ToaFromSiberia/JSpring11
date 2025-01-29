package mr.demonid.service.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Ответ на запрос реквизитов пользователя от UserService
 */
@Data
@AllArgsConstructor
public class UserPayInfo {
    private long userId;
    private long accountId;
    private BigDecimal balance;
}
