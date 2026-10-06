package ru.phyllosedis.platform.app.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.phyllosedis.platform.banking.api.exception.account.AccountNotFoundException;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void accountNotFoundMapsTo404() {
        ResponseEntity<ErrorResponseDto> result =
                handler.handleAccountNotFound(new AccountNotFoundException(UUID.randomUUID()));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getHttpStatus()).isEqualTo(404);
        assertThat(result.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void noSuchElementMapsTo404() {
        ResponseEntity<ErrorResponseDto> result =
                handler.handleNotFound(new NoSuchElementException("auth user not found"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().getDescription()).contains("auth user not found");
    }

    @Test
    void illegalArgumentMapsTo400() {
        ResponseEntity<ErrorResponseDto> result =
                handler.handleBadRequest(new IllegalArgumentException("content must not be blank"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().getError()).isEqualTo("BAD_REQUEST");
    }

    @Test
    void unexpectedMapsTo500WithoutLeak() {
        ResponseEntity<ErrorResponseDto> result =
                handler.handleUnexpected(new RuntimeException("select * from users"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody().getDescription()).doesNotContain("select");
    }
}
