package ru.phyllosedis.platform.banking.api.exception.user;

public class UserIdCannotBeNullException extends RuntimeException {
    public UserIdCannotBeNullException() {
        super("Переданный id пользователя является пустым");
    }
}
