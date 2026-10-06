package ru.phyllosedis.platform.app.controller.banking.transaction;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferAccountNumberRequestDto;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferConversionRequestDto;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferIdRequestDto;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferResponseDto;
import ru.phyllosedis.platform.banking.api.service.TransactionService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/banking/transaction")
public class TransactionController {
    private final TransactionService transactionService;


    @PostMapping("/transfer/byId")
    public TransactionTransferResponseDto transferById(@RequestBody TransactionTransferIdRequestDto dto) {
        // TODO временно фиксируемся на рублях для тестов
        return transactionService.transferById(dto.getFrom(), dto.getTo(), dto.getAmount(), dto.getType(), dto.getCurrency());
    }

    @PostMapping("/transfer/byAccountNumber")
    public TransactionTransferResponseDto transferByAccountNumber(@RequestBody TransactionTransferAccountNumberRequestDto dto) {
        // TODO временно фиксируемся на рублях для тестов
        return transactionService.transferByAccountNumber(dto.getFrom(), dto.getTo(), dto.getAmount(), dto.getType(), dto.getCurrency());
    }

    @PostMapping("/transfer/withConversion")
    public TransactionTransferResponseDto transferWithConversion(@RequestBody TransactionTransferConversionRequestDto dto) {
        return transactionService.transferWithConversion(dto.getFrom(), dto.getTo(), dto.getAmount(), dto.getType());
    }
}
