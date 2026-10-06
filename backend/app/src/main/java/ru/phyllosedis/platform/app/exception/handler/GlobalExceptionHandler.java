package ru.phyllosedis.platform.app.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.phyllosedis.platform.banking.api.exception.account.AccountNotFoundException;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

/**
 * Единая точка маппинга доменных ошибок в HTTP.
 * Работает и для реактивных эндпоинтов (Mono/Flux): ошибка в сигнале
 * тоже проходит через эти хендлеры.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountNotFound(AccountNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NoSuchElementException ex) {
        return body(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(IllegalArgumentException ex) {
        return body(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception ex) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error");
    }

    private ResponseEntity<ErrorResponseDto> body(HttpStatus status, String error, String description) {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .httpStatus(status.value())
                .error(error)
                .description(description)
                .timestamp(ZonedDateTime.now())
                .build();
        return new ResponseEntity<>(dto, status);
    }
}
