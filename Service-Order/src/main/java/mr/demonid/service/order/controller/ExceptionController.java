package mr.demonid.service.order.controller;

import mr.demonid.service.order.exceptions.OrderException;
import mr.demonid.service.order.exceptions.OrderThrowedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(OrderException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String catalogException(OrderException e) {
        if (e instanceof OrderThrowedException) {
            return e.getMessage();
        }
        return "Ошибка: " + LocalDateTime.now() + ": " + e.getMessage();
    }

}
