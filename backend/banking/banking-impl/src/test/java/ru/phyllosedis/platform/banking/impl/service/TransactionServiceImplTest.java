package ru.phyllosedis.platform.banking.impl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.account.AccountStatus;
import ru.phyllosedis.platform.banking.api.dto.account.AccountType;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionFailedReason;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionStatus;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionType;
import ru.phyllosedis.platform.banking.api.dto.transaction.rest.TransactionTransferResponseDto;
import ru.phyllosedis.platform.banking.api.exception.account.AccountNotFoundException;
import ru.phyllosedis.platform.banking.impl.model.entity.Account;
import ru.phyllosedis.platform.banking.impl.model.entity.User;
import ru.phyllosedis.platform.banking.impl.repository.AccountRepository;
import ru.phyllosedis.platform.banking.impl.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl service;

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private Account account(UUID id, String number, Currency currency, BigDecimal amount,
                            AccountStatus status, AccountType type) {
        return Account.builder()
                .id(id)
                .user(User.builder().id(USER_ID).name("test").build())
                .accountNumber(number)
                .currency(currency)
                .status(status)
                .type(type)
                .amount(amount)
                .build();
    }

    private void stubLock(Account first, Account second) {
        when(accountRepository.findByIdForUpdate(first.getId())).thenReturn(Optional.of(first));
        when(accountRepository.findByIdForUpdate(second.getId())).thenReturn(Optional.of(second));
    }

    private void stubSaveThrough() {
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void successByIdMovesMoney() {
        Account from = account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "408810000001", Currency.RUB, new BigDecimal("1000.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        Account to = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408810000002", Currency.RUB, new BigDecimal("100.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        stubLock(from, to);
        stubSaveThrough();

        TransactionTransferResponseDto result = service.transferById(
                from.getId(), to.getId(), new BigDecimal("250.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETE);
        assertThat(from.getAmount()).isEqualByComparingTo(new BigDecimal("750.00"));
        assertThat(to.getAmount()).isEqualByComparingTo(new BigDecimal("350.00"));
        verify(transactionRepository, times(2)).save(any());
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    void sameIdFailsWithoutLocking() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

        TransactionTransferResponseDto result = service.transferById(
                id, id, new BigDecimal("10.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(result.getReason()).isEqualTo(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT);
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    void zeroAndNullAmountFail() {
        Account from = account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "408810000001", Currency.RUB, new BigDecimal("1000.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        Account to = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408810000002", Currency.RUB, new BigDecimal("100.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        stubLock(from, to);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(service.transferById(from.getId(), to.getId(),
                BigDecimal.ZERO, TransactionType.TRANSFER, Currency.RUB).getReason())
                .isEqualTo(TransactionFailedReason.AMOUNT_CANNOT_BE_BELOW_ZERO);
        assertThat(service.transferById(from.getId(), to.getId(),
                new BigDecimal("-5.00"), TransactionType.TRANSFER, Currency.RUB).getReason())
                .isEqualTo(TransactionFailedReason.AMOUNT_CANNOT_BE_BELOW_ZERO);
        assertThat(service.transferById(from.getId(), to.getId(),
                null, TransactionType.TRANSFER, Currency.RUB).getReason())
                .isEqualTo(TransactionFailedReason.AMOUNT_CANNOT_BE_BELOW_ZERO);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void currencyMismatchFails() {
        Account from = account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "408810000001", Currency.RUB, new BigDecimal("1000.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        Account to = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408840000002", Currency.USD, new BigDecimal("100.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        stubLock(from, to);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TransactionTransferResponseDto result = service.transferById(
                from.getId(), to.getId(), new BigDecimal("10.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(result.getReason()).isEqualTo(TransactionFailedReason.ACCOUNT_CURRENCY_NOT_SAME);
        assertThat(from.getAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void notEnoughMoneyFailsButEmissionBypasses() {
        Account poor = account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "408810000001", Currency.RUB, new BigDecimal("100.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        Account to = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408810000002", Currency.RUB, new BigDecimal("0.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        stubLock(poor, to);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TransactionTransferResponseDto failed = service.transferById(
                poor.getId(), to.getId(), new BigDecimal("500.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(failed.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(failed.getReason()).isEqualTo(TransactionFailedReason.NOT_ENOUGH_MONEY);
    }

    @Test
    void emissionAccountMayEmitBeyondBalance() {
        Account emission = account(UUID.fromString("00000000-0000-0000-0000-000000000000"),
                "999810000000", Currency.RUB, new BigDecimal("0.00"), AccountStatus.ACTIVE, AccountType.SYSTEM_CB_EMISSION);
        Account to = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408810000002", Currency.RUB, new BigDecimal("0.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        when(accountRepository.findByIdForUpdate(emission.getId())).thenReturn(Optional.of(emission));
        when(accountRepository.findByIdForUpdate(to.getId())).thenReturn(Optional.of(to));
        stubSaveThrough();

        TransactionTransferResponseDto result = service.transferById(
                emission.getId(), to.getId(), new BigDecimal("500.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETE);
        assertThat(to.getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    void inactiveAccountFails() {
        Account frozen = account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "408810000001", Currency.RUB, new BigDecimal("1000.00"), AccountStatus.FROZEN, AccountType.INDIVIDUAL);
        Account to = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408810000002", Currency.RUB, new BigDecimal("0.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        stubLock(frozen, to);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TransactionTransferResponseDto result = service.transferById(
                frozen.getId(), to.getId(), new BigDecimal("10.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(result.getReason()).isEqualTo(TransactionFailedReason.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    void missingAccountThrows() {
        UUID missing = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID present = UUID.fromString("00000000-0000-0000-0000-000000000002");
        Account to = account(present, "408810000002", Currency.RUB,
                new BigDecimal("0.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        when(accountRepository.findByIdForUpdate(missing)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transferById(
                missing, present, new BigDecimal("10.00"), TransactionType.TRANSFER, Currency.RUB))
                .isInstanceOf(AccountNotFoundException.class);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void locksAreAcquiredInSortedOrder() {
        // from > to: блокировка все равно идет от меньшего UUID к большему (защита от deadlock).
        Account bigger = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408810000002", Currency.RUB, new BigDecimal("1000.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        Account smaller = account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "408810000001", Currency.RUB, new BigDecimal("100.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        when(accountRepository.findByIdForUpdate(smaller.getId())).thenReturn(Optional.of(smaller));
        when(accountRepository.findByIdForUpdate(bigger.getId())).thenReturn(Optional.of(bigger));
        stubSaveThrough();

        TransactionTransferResponseDto result = service.transferById(
                bigger.getId(), smaller.getId(), new BigDecimal("100.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETE);
        InOrder order = inOrder(accountRepository);
        order.verify(accountRepository).findByIdForUpdate(smaller.getId());
        order.verify(accountRepository).findByIdForUpdate(bigger.getId());
        assertThat(bigger.getAmount()).isEqualByComparingTo(new BigDecimal("900.00"));
        assertThat(smaller.getAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    void successByAccountNumber() {
        Account from = account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "408810000001", Currency.RUB, new BigDecimal("500.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        Account to = account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "408810000002", Currency.RUB, new BigDecimal("0.00"), AccountStatus.ACTIVE, AccountType.INDIVIDUAL);
        when(accountRepository.findByAccountNumberForUpdate("408810000001")).thenReturn(Optional.of(from));
        when(accountRepository.findByAccountNumberForUpdate("408810000002")).thenReturn(Optional.of(to));
        stubSaveThrough();

        TransactionTransferResponseDto result = service.transferByAccountNumber(
                "408810000001", "408810000002", new BigDecimal("200.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETE);
        assertThat(from.getAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(to.getAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    void sameAccountNumberFailsWithoutLocking() {
        TransactionTransferResponseDto result = service.transferByAccountNumber(
                "408810000001", "408810000001", new BigDecimal("10.00"), TransactionType.TRANSFER, Currency.RUB);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(result.getReason()).isEqualTo(TransactionFailedReason.CANNOT_TRANSFER_ON_THE_SAME_ACCOUNT);
        verifyNoInteractions(accountRepository, transactionRepository);
    }
}
