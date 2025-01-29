package mr.demonid.service.catalog.controllers;

import mr.demonid.service.catalog.exceptions.CatalogException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(CatalogException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String catalogException(CatalogException e) {
        return "Ошибка: " + LocalDateTime.now() + ": " + e.getMessage();
    }

}
