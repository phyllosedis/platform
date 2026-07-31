package ru.phyllosedis.platform.app.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.phyllosedis.platform.banking.api.dto.AccountBalanceDto;
import ru.phyllosedis.platform.banking.api.service.BankingService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/banking")
public class BankingController {

    private final BankingService bankingService;

    @GetMapping("/balance/{accountNumber}")
    public AccountBalanceDto getBalance(@PathVariable("accountNumber") String accountNumber) {
        return bankingService.getBalance(accountNumber);
    }

}
