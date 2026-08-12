package ru.phyllosedis.platform.banking.impl.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.account.AccountType;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionFailedReason;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionStatus;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionType;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferResponseDto;
import ru.phyllosedis.platform.banking.api.exception.account.AccountNotFoundException;
import ru.phyllosedis.platform.banking.api.service.TransactionService;
import ru.phyllosedis.platform.banking.impl.model.entity.Account;
import ru.phyllosedis.platform.banking.impl.model.entity.Transaction;
import ru.phyllosedis.platform.banking.impl.repository.AccountRepository;
import ru.phyllosedis.platform.banking.impl.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private TransactionTransferResponseDto validate(Account from, Account to, BigDecimal amount, TransactionType type, Currency currency) {

        TransactionTransferResponseDto.TransactionTransferResponseDtoBuilder ttrd = TransactionTransferResponseDto.builder();
        ttrd.status(TransactionStatus.FAILED);

        if (!from.isActive() || !to.isActive()) {
            return ttrd
                    .reason(TransactionFailedReason.ACCOUNT_NOT_ACTIVE)
                    .description(TransactionFailedReason.ACCOUNT_NOT_ACTIVE.getDescription())
                    .build();
        }

        if (from.equals(to)) {
            return ttrd
                    .reason(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT)
                    .description(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT.getDescription())
                    .build();
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return ttrd
                    .reason(TransactionFailedReason.AMOUNT_CANNOT_BE_BELOW_ZERO)
                    .description(TransactionFailedReason.AMOUNT_CANNOT_BE_BELOW_ZERO.getDescription())
                    .build();
        }

        if (!from.getCurrency().equals(currency) || !to.getCurrency().equals(currency)) {
            return ttrd
                    .reason(TransactionFailedReason.ACCOUNT_CURRENCY_NOT_SAME)
                    .description(String.format("Счёт отправителя имеет валюту %s. Счёт получателя имеет валюту %s.", from.getCurrency(), to.getCurrency()))
                    .build();
        }

        if (!from.getType().equals(AccountType.SYSTEM_CB_EMISSION) && from.getAmount().compareTo(amount) < 0) {
            return ttrd
                    .reason(TransactionFailedReason.NOT_ENOUGH_MONEY)
                    .description(String.format("На счёте %s:%s недостаточно средств", from.getId(), from.getAccountNumber()))
                    .build();
        }

        return new TransactionTransferResponseDto(TransactionStatus.PENDING, null, "");
    }

    private TransactionTransferResponseDto transfer(Account from, Account to, BigDecimal amount, TransactionType type, Currency currency) {
        // todo определиться с currency, проверяем счёта from и to на соответствие currency, иначе эксепшн
        // чтобы статус аккаунта был активным
        // чтобы аккаунт был с соответствующим currency. на первое время перевода валют из одного счёта в другой не будет доступно
        // чтобы amount'а хватало для вычитания из accountFrom

        Transaction.TransactionBuilder txb = Transaction.builder()
                .fromAccount(from)
                .toAccount(to)
                .amount(amount)
                .type(type);

        TransactionTransferResponseDto validated = validate(from, to, amount, type, currency);
        if (validated.getStatus().equals(TransactionStatus.FAILED)) {
            txb.status(TransactionStatus.FAILED);
            transactionRepository.save(txb.build());
            return validated;
        }
        Transaction tx = txb
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(tx);

        from.setAmount(from.getAmount().subtract(amount));
        to.setAmount(to.getAmount().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        tx.setStatus(TransactionStatus.COMPLETE);
        transactionRepository.save(tx);

        return new TransactionTransferResponseDto(tx.getStatus(), null, "");
    }


    @Override
    @Transactional
    public TransactionTransferResponseDto transferById(UUID from, UUID to, BigDecimal amount, TransactionType type, Currency currency) {
        Account fromAccount = accountRepository.findById(from).orElseThrow(() -> new AccountNotFoundException(from));
        Account toAccount = accountRepository.findById(to).orElseThrow(() -> new AccountNotFoundException(to));

        return transfer(fromAccount, toAccount, amount, type, currency);
    }

    @Override
    @Transactional
    public TransactionTransferResponseDto transferByAccountNumber(String from, String to, BigDecimal amount, TransactionType type, Currency currency) {
        Account fromAccount = accountRepository.findByAccountNumber(from).orElseThrow(() -> new AccountNotFoundException(from));
        Account toAccount = accountRepository.findByAccountNumber(to).orElseThrow(() -> new AccountNotFoundException(to));

        return transfer(fromAccount, toAccount, amount, type, currency);
    }
}
