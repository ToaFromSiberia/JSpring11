package mr.demonid.web.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Данные о всех пользователях
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
