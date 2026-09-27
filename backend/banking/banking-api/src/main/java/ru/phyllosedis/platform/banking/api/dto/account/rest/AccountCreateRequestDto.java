package ru.phyllosedis.platform.banking.api.dto.account.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.account.AccountType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequestDto {
    private UUID userId;
    private AccountType accountType;
    private Currency currency;
}
