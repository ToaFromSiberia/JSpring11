package mr.demonid.service.user.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Класс с информацией о пользователе для веб-интерфейса.
 */
@Data
public class UserInfo {
    private Long id;
    private String username;
    private BigDecimal balance;

    public UserInfo(Long id, String username, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }
}
