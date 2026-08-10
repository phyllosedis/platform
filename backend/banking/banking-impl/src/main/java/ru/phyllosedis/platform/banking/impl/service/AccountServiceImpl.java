package ru.phyllosedis.platform.banking.impl.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.platform.banking.api.dto.AccountBalanceResponseDto;
import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.account.AccountType;
import ru.phyllosedis.platform.banking.api.exception.AccountNotFoundException;
import ru.phyllosedis.platform.banking.api.exception.UserNotFoundException;
import ru.phyllosedis.platform.banking.api.service.AccountService;
import ru.phyllosedis.platform.banking.impl.model.entity.Account;
import ru.phyllosedis.platform.banking.impl.model.entity.User;
import ru.phyllosedis.platform.banking.impl.repository.AccountRepository;
import ru.phyllosedis.platform.banking.impl.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public AccountBalanceResponseDto getBalance(String accountNumber) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        Account account = accountOptional.orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return new AccountBalanceResponseDto(accountNumber, account.getAmount(), account.getCurrency());
    }

    @Override
    public UUID createAccount(UUID userId, AccountType accountType, Currency currency) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Account account = Account.builder()
                .type(accountType)
                .currency(currency)
                .user(user)
                .build();
        accountRepository.save(account);
        return account.getId();
    }
}
