package ru.phyllosedis.platform.banking.api.dto.transaction.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionTransferConversionRequestDto {
    UUID from;
    UUID to;
    BigDecimal amount;
    TransactionType type;
}
