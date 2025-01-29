package mr.demonid.service.payment.links;

import mr.demonid.service.payment.dto.PaymentRequest;
import mr.demonid.service.payment.dto.UserPayInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    /**
     * Запрос баланса счета пользователя.
     * @param id Пользователь
     */
    @GetMapping("/api/user/account/{id}")
    ResponseEntity<UserPayInfo> getAccount(@PathVariable Long id);

    /**
     * Перевод средств с одного счета на другой.
     * @param payInfo Информация, от кого, кому и сколько перевести
     */
    @PostMapping("/api/user/account/transaction")
    ResponseEntity<Void> transaction(@RequestBody PaymentRequest payInfo);


}
