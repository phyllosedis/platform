package ru.phyllosedis.platform.banking.impl.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionStatus;
import ru.phyllosedis.platform.banking.api.dto.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"transaction\"")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private TransactionType type;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private TransactionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal amount;
}
