package ru.phyllosedis.platform.banking.api.service;

import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionType;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {
    TransactionTransferResponseDto transferById(UUID fromAccount, UUID toAccount, BigDecimal amount, TransactionType type, Currency currency);

    TransactionTransferResponseDto transferByAccountNumber(String fromAccount, String toAccount, BigDecimal amount, TransactionType type, Currency currency);

    /**
     * Перевод от пользователя в заданной валюте: счёт отправителя подбирается
     * по паре (user, currency). Дальше — обычный {@code transferById}
     * с упорядоченными блокировками.
     */
    TransactionTransferResponseDto transferFromUserByCurrency(UUID fromUserId, UUID toAccount, BigDecimal amount, TransactionType type, Currency currency);
}
