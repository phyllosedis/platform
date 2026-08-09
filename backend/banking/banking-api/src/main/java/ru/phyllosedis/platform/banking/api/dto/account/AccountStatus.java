package ru.phyllosedis.platform.banking.api.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatus {
    ACTIVE("Активен"),     // 0 - Активен (счет работает в обычном режиме, все операции разрешены)
    FROZEN("Заморожен / Заблокирован"),     // 1 - Заморожен / Заблокирован (деньги на счету видны, но тратить их нельзя)
    SUSPENDED("Приостановлен"),  // 2 - Приостановлен (временный технический статус, например, до верификации)
    CLOSED("Закрыт")      // 3 - Закрыт (счет архивирован, любые операции по нему запрещены навсегда)
    ;
    private final String description;
}
