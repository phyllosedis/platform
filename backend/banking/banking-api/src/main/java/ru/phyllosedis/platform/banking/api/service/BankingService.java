package ru.phyllosedis.platform.banking.api.service;

import ru.phyllosedis.platform.banking.api.dto.AccountBalanceDto;

public interface BankingService {
    AccountBalanceDto getBalance(String accountNumber);
}
