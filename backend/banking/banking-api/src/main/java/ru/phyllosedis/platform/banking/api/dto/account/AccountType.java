package ru.phyllosedis.platform.banking.api.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    SYSTEM_CB_EMISSION("Эмиссионный счёт центрального банка"), // 0 - Эмиссия ЦБ
    SYSTEM_BANK_CASH("Касса / банкомат банка"),   // 1 - Касса/Банкомат банка
    ENTERPRISE("Юридическое лицо / предприятие"),         // 2 - Юрлица / Предприятия
    MERCHANT("Магазин"),           // 3 - Магазины
    INDIVIDUAL("Обычный человек")          // 4 - Обычные люди
    ;
    private final String description;
}
