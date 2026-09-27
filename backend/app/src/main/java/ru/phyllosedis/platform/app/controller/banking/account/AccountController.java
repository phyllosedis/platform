package ru.phyllosedis.platform.app.controller.banking.account;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.phyllosedis.platform.banking.api.dto.account.AccountStatus;
import ru.phyllosedis.platform.banking.api.dto.account.rest.AccountBalanceResponseDto;
import ru.phyllosedis.platform.banking.api.dto.account.rest.AccountCreateRequestDto;
import ru.phyllosedis.platform.banking.api.dto.account.rest.AccountExistsResponseDto;
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

    @GetMapping("/exists/byAccountNumber")
    public AccountExistsResponseDto existsByAccountNumber(@RequestParam("accountNumber") String accountNumber) {
        return accountService.existsByAccountNumber(accountNumber);
    }

    @GetMapping("/exists/byId")
    public AccountExistsResponseDto existsByUUID(@RequestParam("id") UUID id) {
        return accountService.existsById(id);
    }

    @GetMapping("/status/byId")
    public ResponseEntity<AccountStatus> getAccountStatus(@RequestParam("id") UUID id) {
        AccountStatus accountStatus = accountService.getAccountStatus(id);
        return new ResponseEntity<>(accountStatus, accountStatus == null ? HttpStatus.NOT_FOUND : HttpStatus.FOUND);
    }

    @GetMapping("/status/byAccountNumber")
    public ResponseEntity<AccountStatus> getAccountStatus(@RequestParam("accountNumber") String accountNumber) {
        AccountStatus accountStatus = accountService.getAccountStatus(accountNumber);
        return new ResponseEntity<>(accountStatus, accountStatus == null ? HttpStatus.NOT_FOUND : HttpStatus.FOUND);
    }
}
