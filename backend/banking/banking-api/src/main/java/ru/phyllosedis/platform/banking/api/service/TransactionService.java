package ru.phyllosedis.platform.banking.api.service;

import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionType;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {
    TransactionTransferResponseDto transferById(UUID fromAccount, UUID toAccount, BigDecimal amount, TransactionType type, Currency currency);

    TransactionTransferResponseDto transferByAccountNumber(String fromAccount, String toAccount, BigDecimal amount, TransactionType type, Currency currency);
}
