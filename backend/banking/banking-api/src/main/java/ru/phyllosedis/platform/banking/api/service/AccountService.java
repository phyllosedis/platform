package ru.phyllosedis.platform.banking.api.service;

import ru.phyllosedis.platform.banking.api.dto.account.rest.AccountBalanceResponseDto;
import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.account.rest.AccountExistsResponseDto;
import ru.phyllosedis.platform.banking.api.dto.account.AccountStatus;
import ru.phyllosedis.platform.banking.api.dto.account.AccountType;

import java.util.UUID;

public interface AccountService {
    AccountBalanceResponseDto getBalance(String accountNumber);

    UUID createAccount(UUID userId, AccountType accountType, Currency currency);

    AccountExistsResponseDto existsById(UUID id);
    AccountExistsResponseDto existsByAccountNumber(String accountNumber);

    AccountStatus getAccountStatus(UUID id);
    AccountStatus getAccountStatus(String accountNumber);
}
