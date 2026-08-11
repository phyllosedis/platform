package ru.phyllosedis.platform.banking.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query(value = "select status from Account where id = :id")
    AccountStatus getAccountStatus(UUID id);

    @Query(value = "select status from Account where accountNumber = :accountNumber")
    AccountStatus getAccountStatus(String accountNumber);

    @Query(value = "select new ru.phyllosedis.platform.banking.api.dto.account.rest.AccountExistsResponseDto(id, accountNumber) from Account where accountNumber = :accountNumber")
    Optional<AccountExistsResponseDto> existsByAccountNumber(String accountNumber);

    @Query(value = "select new ru.phyllosedis.platform.banking.api.dto.account.rest.AccountExistsResponseDto(id, accountNumber) from Account where id = :id")
    Optional<AccountExistsResponseDto> existsByUUID(UUID id);
}
