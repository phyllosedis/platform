package ru.phyllosedis.platform.app.controller.banking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.phyllosedis.platform.banking.api.dto.AccountBalanceResponseDto;
import ru.phyllosedis.platform.banking.api.dto.AccountCreateRequestDto;
import ru.phyllosedis.platform.banking.api.service.AccountService;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/banking/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<UUID> create(@RequestBody AccountCreateRequestDto dto) {
        UUID uuid = accountService.createAccount(dto.getUserId(), dto.getAccountType(), dto.getCurrency());
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

    @GetMapping("/balance/{accountNumber}")
    public AccountBalanceResponseDto getBalance(@PathVariable String accountNumber) {
        return accountService.getBalance(accountNumber);
    }

}
