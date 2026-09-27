package ru.phyllosedis.platform.banking.api.exception.user;

public class UserNameCannotBeNullException extends RuntimeException {
    public UserNameCannotBeNullException(String message) {
        super(String.format("Имя пользователя не может быть пустым. Переданное значение: '%s'", message == null ? "null" : message));
    }
}
