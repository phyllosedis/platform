package ru.phyllosedis.platform.banking.impl;

import org.springframework.stereotype.Service;
import ru.phyllosedis.platform.banking.api.dto.AccountBalanceDto;
import ru.phyllosedis.platform.banking.api.service.BankingService;

import java.math.BigDecimal;

@Service
public class BankingServiceImpl implements BankingService {

    @Override
    public AccountBalanceDto getBalance(String accountNumber) {
        return new AccountBalanceDto(accountNumber, new BigDecimal("15950.50"));
    }
}
