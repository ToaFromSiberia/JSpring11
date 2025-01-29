package mr.demonid.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Класс для запроса на регистрацию нового пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest implements Serializable
{
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}
