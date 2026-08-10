package ru.phyllosedis.platform.app.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.phyllosedis.platform.app.controller.BankingController;
import ru.phyllosedis.platform.banking.api.exception.AccountNotFoundException;

import java.time.ZonedDateTime;

@RestControllerAdvice(assignableTypes = {BankingController.class})
public class BankingControllerExceptionHandler {

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountNotFound(AccountNotFoundException ex) {
        ErrorResponseDto accountNotFound = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                "ACCOUNT_NOT_FOUND",
                ex.getMessage(),
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(accountNotFound, HttpStatus.NOT_FOUND);
    }
}
