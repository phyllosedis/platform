package ru.phyllosedis.platform.banking.api.service;

import ru.phyllosedis.platform.banking.api.dto.account.AccountBalanceResponseDto;
import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.account.AccountType;

import java.util.UUID;

public interface AccountService {
    AccountBalanceResponseDto getBalance(String accountNumber);

    UUID createAccount(UUID userId, AccountType accountType, Currency currency);
}
