package ru.phyllosedis.platform.banking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceResponseDto {
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
}
