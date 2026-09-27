package ru.phyllosedis.platform.banking.api.dto.account.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AccountExistsResponseDto {
    private UUID id;
    private String accountNumber;
}
