package ru.phyllosedis.platform.app.exception.handler.banking.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.phyllosedis.platform.app.controller.banking.user.UserController;
import ru.phyllosedis.platform.app.exception.handler.ErrorResponseDto;
import ru.phyllosedis.platform.banking.api.exception.user.UserIdCannotBeNullException;
import ru.phyllosedis.platform.banking.api.exception.user.UserNameCannotBeNullException;
import ru.phyllosedis.platform.banking.api.exception.user.UserNotFoundException;
import ru.phyllosedis.platform.banking.api.service.AccountService;

import java.time.ZonedDateTime;

@RestControllerAdvice(assignableTypes = {UserController.class, AccountService.class})
public class UserControllerExceptionHandler {

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponseDto userNotFound = ErrorResponseDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .description("USER_NOT_FOUND")
                .error(ex.getMessage())
                .timestamp(ZonedDateTime.now())
                .build();
        return new ResponseEntity<>(userNotFound, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserNameCannotBeNullException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNameCannotBeNull(UserNameCannotBeNullException ex) {
        ErrorResponseDto userNameCannotBeNull = ErrorResponseDto.builder()
                .description("USER_NAME_CANNOT_BE_NULL")
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timestamp(ZonedDateTime.now())
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(userNameCannotBeNull, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserIdCannotBeNullException.class)
    public ResponseEntity<ErrorResponseDto> handleUserIdCannotBeNull(UserIdCannotBeNullException ex) {
        ErrorResponseDto userIdCannotBeNull = ErrorResponseDto.builder()
                .description("USER_ID_CANNOT_BE_NULL")
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timestamp(ZonedDateTime.now())
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(userIdCannotBeNull, HttpStatus.BAD_REQUEST);
    }
}
