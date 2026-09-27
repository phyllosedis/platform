package ru.phyllosedis.platform.banking.api.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    DEPOSIT("Пополнение"),   // 0 - Внесение наличных (from: банкомат/касса -> to: пользователь)
    WITHDRAW("Снятие"),      // 1 - Выдача наличных (from: пользователь -> to: банкомат/касса)
    TRANSFER("Перевод")      // 2 - Внутрисистемный перевод (from: пользователь/бизнес -> to: пользователь/бизнес)
    ;

    private final String description;
}
