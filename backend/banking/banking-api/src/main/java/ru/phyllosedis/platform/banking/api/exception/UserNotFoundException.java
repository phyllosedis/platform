package ru.phyllosedis.platform.banking.api.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super(String.format("Пользователь с ID %s не найден", userId));
    }
}
