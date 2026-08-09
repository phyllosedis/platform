package ru.phyllosedis.platform.banking.api.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionStatus {
    PENDING("Ожидает обработки"),   // 0 - Транзакция создана, но еще не валидирована и не проведена
    COMPLETE("Успешно проведена"),  // 1 - Балансы обновлены, деньги успешно дошли до получателя
    FAILED("Отклонена / Ошибка")    // 2 - Транзакция отменена (нехватка средств, заблокированный счет и т.д.)
    ;

    private final String description;
}
