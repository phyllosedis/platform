package ru.phyllosedis.platform.app.exception.handler.banking.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.phyllosedis.platform.app.controller.banking.account.AccountController;
import ru.phyllosedis.platform.app.exception.handler.ErrorResponseDto;
import ru.phyllosedis.platform.banking.api.exception.account.AccountNotFoundException;

import java.time.ZonedDateTime;

@RestControllerAdvice(assignableTypes = {AccountController.class})
public class AccountControllerExceptionHandler {

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountNotFound(AccountNotFoundException ex) {
        ErrorResponseDto accountNotFound = ErrorResponseDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .description("ACCOUNT_NOT_FOUND")
                .error(ex.getMessage())
                .timestamp(ZonedDateTime.now())
                .build();
        return new ResponseEntity<>(accountNotFound, HttpStatus.NOT_FOUND);
    }
}
