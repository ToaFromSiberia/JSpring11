package mr.demonid.service.payment.controllers;

import lombok.AllArgsConstructor;
import mr.demonid.service.payment.dto.PaymentRequest;
import mr.demonid.service.payment.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class ApiController {

    PaymentService paymentService;

    /**
     * Транзакция средств от пользователя в магазин.
     */
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody PaymentRequest request) {
        // проверяем возможность трансфера средств
        paymentService.checkTransfer(request);
        // и запрашиваем перевод средств
        paymentService.transfer(request);
        return ResponseEntity.ok().build();
    }
}
