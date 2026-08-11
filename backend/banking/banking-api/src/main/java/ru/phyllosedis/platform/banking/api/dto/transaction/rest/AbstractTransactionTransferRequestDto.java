package ru.phyllosedis.platform.banking.api.dto.transaction.rest;

import lombok.Data;
import ru.phyllosedis.platform.banking.api.dto.Currency;

import java.math.BigDecimal;

@Data
public abstract class AbstractTransactionTransferRequestDto<T> {
    T from;
    T to;
    BigDecimal amount;
    Currency currency;
}
