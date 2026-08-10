package ru.phyllosedis.platform.banking.impl.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.platform.banking.api.dto.AccountBalanceDto;
import ru.phyllosedis.platform.banking.api.service.BankingService;
import ru.phyllosedis.platform.banking.api.exception.AccountNotFoundException;
import ru.phyllosedis.platform.banking.impl.model.entity.Account;
import ru.phyllosedis.platform.banking.impl.repository.AccountRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BankingServiceImpl implements BankingService {

    private final AccountRepository accountRepository;

    @Override
    public AccountBalanceDto getBalance(String accountNumber) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        Account account = accountOptional.orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return new AccountBalanceDto(accountNumber, account.getAmount(), account.getCurrency());
    }
}
