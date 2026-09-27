package ru.phyllosedis.platform.banking.api.dto.transaction.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionFailedReason;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionStatus;

@Data
@Builder
@AllArgsConstructor
public class TransactionTransferResponseDto {
    TransactionStatus status;
    TransactionFailedReason reason;
    String description;
}
