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
import java.util.List;
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

        if (from.getId() != null && from.getId().equals(to.getId())) {
            return ttrd
                    .reason(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT)
                    .description(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT.getDescription())
                    .build();
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
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
    @Transactional("bankingTransactionManager")
    public TransactionTransferResponseDto transferById(UUID from, UUID to, BigDecimal amount, TransactionType type, Currency currency) {
        if (from.equals(to)) {
            return TransactionTransferResponseDto.builder()
                    .status(TransactionStatus.FAILED)
                    .reason(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT)
                    .description(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT.getDescription())
                    .build();
        }

        // Фиксированный порядок блокировки по UUID — защита от deadlock
        // при встречных переводах A->B и B->A.
        UUID firstId = from.compareTo(to) < 0 ? from : to;
        UUID secondId = from.compareTo(to) < 0 ? to : from;

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new AccountNotFoundException(firstId));
        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new AccountNotFoundException(secondId));

        Account fromAccount = first.getId().equals(from) ? first : second;
        Account toAccount = first.getId().equals(from) ? second : first;

        return transfer(fromAccount, toAccount, amount, type, currency);
    }

    @Override
    @Transactional("bankingTransactionManager")
    public TransactionTransferResponseDto transferByAccountNumber(String from, String to, BigDecimal amount, TransactionType type, Currency currency) {
        if (from.equals(to)) {
            return TransactionTransferResponseDto.builder()
                    .status(TransactionStatus.FAILED)
                    .reason(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT)
                    .description(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT.getDescription())
                    .build();
        }

        // Фиксированный порядок блокировки по номеру счёта — защита от deadlock.
        String firstNumber = from.compareTo(to) < 0 ? from : to;
        String secondNumber = from.compareTo(to) < 0 ? to : from;

        Account first = accountRepository.findByAccountNumberForUpdate(firstNumber)
                .orElseThrow(() -> new AccountNotFoundException(firstNumber));
        Account second = accountRepository.findByAccountNumberForUpdate(secondNumber)
                .orElseThrow(() -> new AccountNotFoundException(secondNumber));

        Account fromAccount = first.getAccountNumber().equals(from) ? first : second;
        Account toAccount = first.getAccountNumber().equals(from) ? second : first;

        return transfer(fromAccount, toAccount, amount, type, currency);
    }

    @Override
    @Transactional("bankingTransactionManager")
    public TransactionTransferResponseDto transferFromUserByCurrency(UUID fromUserId, UUID to, BigDecimal amount, TransactionType type, Currency currency) {
        // Подбор счёта без блокировки; сам перевод ниже идет через transferById,
        // который перечитывает оба счёта под PESSIMISTIC_WRITE и там же валидирует.
        List<Account> candidates = accountRepository.findByUserIdAndCurrency(fromUserId, currency);
        if (candidates.isEmpty()) {
            return TransactionTransferResponseDto.builder()
                    .status(TransactionStatus.FAILED)
                    .reason(TransactionFailedReason.SENDER_HAS_NO_ACCOUNT_IN_CURRENCY)
                    .description(TransactionFailedReason.SENDER_HAS_NO_ACCOUNT_IN_CURRENCY.getDescription())
                    .build();
        }
        if (candidates.size() > 1) {
            return TransactionTransferResponseDto.builder()
                    .status(TransactionStatus.FAILED)
                    .reason(TransactionFailedReason.SENDER_HAS_MULTIPLE_ACCOUNTS_IN_CURRENCY)
                    .description(TransactionFailedReason.SENDER_HAS_MULTIPLE_ACCOUNTS_IN_CURRENCY.getDescription())
                    .build();
        }
        return transferById(candidates.get(0).getId(), to, amount, type, currency);
    }
}
