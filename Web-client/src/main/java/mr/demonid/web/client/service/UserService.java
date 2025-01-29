package mr.demonid.web.client.service;

import feign.FeignException;
import lombok.AllArgsConstructor;
import mr.demonid.web.client.dto.UserInfo;
import mr.demonid.web.client.links.UserServiceClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    UserServiceClient userServiceClient;

    /**
     * Возвращает список всех пользователей
     * @return в случае ошибки вернет пустой список.
     */
    public List<UserInfo> getAllUsers() {
        try {
            return userServiceClient.getAllUsers().getBody();
        } catch (FeignException e) {
            System.out.println("getAllUsers(): Что-то пошло не так: " + e.contentUTF8());
            return new ArrayList<>();
        }
    }

    public UserInfo getUserById(Long id) {
        try {
            return userServiceClient.getUserById(id).getBody();
        } catch (FeignException e) {
            System.out.println("getAllUsers(): Что-то пошло не так: " + e.contentUTF8());
            return new UserInfo(0L, "", BigDecimal.ZERO);
        }
    }
}
