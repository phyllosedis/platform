package ru.phyllosedis.platform.banking.impl.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.phyllosedis.platform.banking.api.dto.transaction.Status;
import ru.phyllosedis.platform.banking.api.dto.transaction.Type;

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
    private Type type;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private ZonedDateTime createdAt;
}
