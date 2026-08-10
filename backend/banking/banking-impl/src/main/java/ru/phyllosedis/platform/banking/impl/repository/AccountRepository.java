package ru.phyllosedis.platform.banking.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.phyllosedis.platform.banking.impl.model.entity.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);
}
