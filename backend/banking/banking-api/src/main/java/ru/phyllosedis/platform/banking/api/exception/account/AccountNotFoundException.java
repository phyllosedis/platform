package ru.phyllosedis.platform.banking.api.exception.account;


import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super(String.format("Банковский счёт с номером %s не найден", accountNumber));
    }

    public AccountNotFoundException(UUID id) {
        super(String.format("Банковский счёт с ID %s не найден", id));
    }
}
