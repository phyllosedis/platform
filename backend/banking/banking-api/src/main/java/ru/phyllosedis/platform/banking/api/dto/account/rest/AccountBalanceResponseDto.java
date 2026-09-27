package ru.phyllosedis.platform.banking.api.dto.account.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.phyllosedis.platform.banking.api.dto.Currency;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceResponseDto {
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
}
