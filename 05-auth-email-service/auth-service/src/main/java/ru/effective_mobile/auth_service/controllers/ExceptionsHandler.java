package ru.effective_mobile.auth_service.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.effective_mobile.auth_service.dto.ErrorResponse;
import ru.effective_mobile.auth_service.exceptions.UserNotFoundException;

@Slf4j
@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("Произошла ошибка: {}", ex.getMessage(), ex);
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("DB_ERROR", ex.getMessage()));
    }
}
