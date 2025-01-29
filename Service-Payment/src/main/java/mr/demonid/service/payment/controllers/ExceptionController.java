package mr.demonid.service.payment.controllers;

import lombok.AllArgsConstructor;
import mr.demonid.service.payment.domain.Payment;
import mr.demonid.service.payment.exceptions.PaymentException;
import mr.demonid.service.payment.exceptions.ThrowedPaymentException;
import mr.demonid.service.payment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@AllArgsConstructor
public class ExceptionController {

    private PaymentRepository paymentRepository;

    @ExceptionHandler(PaymentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String catalogException(PaymentException e) {
        // обновляем статус операции
        Payment payment = paymentRepository.findById(e.getOrderId()).orElse(null);
        if (payment != null) {
            payment.setStatus("FAILED: " + e.getMessage());
            paymentRepository.save(payment);
        }
        // возвращаем ошибку
        if (e instanceof ThrowedPaymentException) {
            return e.getMessage();
        }
        return "Ошибка: " + LocalDateTime.now() + ": " + e.getMessage();
    }

}
