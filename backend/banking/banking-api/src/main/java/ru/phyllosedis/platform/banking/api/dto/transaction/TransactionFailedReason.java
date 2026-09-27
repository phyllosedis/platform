package ru.phyllosedis.platform.banking.api.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionFailedReason {
    NOT_ENOUGH_MONEY("Недостаточно средств"),
    ACCOUNT_NOT_ACTIVE("Счёт неактивный"),
    AMOUNT_CANNOT_BE_BELOW_ZERO("Сумма перевода не может быть меньше нуля"),
    CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT("Нельзя переводить с одного счёта на этот же счёт"),
    ACCOUNT_CURRENCY_NOT_SAME("Денежная единица счёта не совпадает с д.е. у получателя"),
    ;

    private final String description;
}
