package ru.phyllosedis.platform.banking.api.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionFailedReason {
    NOT_ENOUGH_MONEY("Недостаточно средств"),
    ACCOUNT_NOT_ACTIVE("Счёт неактивный")
    ;

    private final String description;
}
