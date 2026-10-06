package ru.phyllosedis.platform.banking.impl.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.phyllosedis.platform.banking.api.dto.account.rest.AccountExistsResponseDto;
import ru.phyllosedis.platform.banking.api.dto.account.AccountStatus;
import ru.phyllosedis.platform.banking.impl.model.entity.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(String accountNumber);

    @Query(value = "select status from Account where id = :id")
    Optional<AccountStatus> getAccountStatus(UUID id);

    @Query(value = "select status from Account where accountNumber = :accountNumber")
    Optional<AccountStatus> getAccountStatus(String accountNumber);

    @Query(value = "select new ru.phyllosedis.platform.banking.api.dto.account.rest.AccountExistsResponseDto(id, accountNumber) from Account where accountNumber = :accountNumber")
    Optional<AccountExistsResponseDto> existsByAccountNumber(String accountNumber);

    @Query(value = "select new ru.phyllosedis.platform.banking.api.dto.account.rest.AccountExistsResponseDto(id, accountNumber) from Account where id = :id")
    Optional<AccountExistsResponseDto> existsByUUID(UUID id);
}
