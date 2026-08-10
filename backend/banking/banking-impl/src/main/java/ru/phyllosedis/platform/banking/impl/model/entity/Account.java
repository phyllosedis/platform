package ru.phyllosedis.platform.banking.impl.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.phyllosedis.platform.banking.api.dto.Currency;
import ru.phyllosedis.platform.banking.api.dto.account.AccountStatus;
import ru.phyllosedis.platform.banking.api.dto.account.AccountType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"account\"")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "currency", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private Currency currency;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private AccountStatus status;

    @Column(name = "\"type\"", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private AccountType type;

    @Column(name = "amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal amount;

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Transaction> fromTransaction;

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Transaction> toTransaction;

    /**
     * Автоматически генерирует уникальный 12-значный номер банковского счета перед сохранением в БД.
     * <p>
     * Алгоритм формирования номера построен на базе упрощенного стандарта ЦБ РФ
     * и состоит из трех логических блоков:
     * </p>
     *
     * <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse;">
     *   <tr bgcolor="#f2f2f2">
     *     <th>Разряды</th>
     *     <th>Назначение</th>
     *     <th>Код / Источник</th>
     *     <th>Описание</th>
     *   </tr>
     *   <tr>
     *     <td><b>1 &ndash; 3</b></td>
     *     <td>Маска типа счета (Балансовый счет)</td>
     *     <td>
     *       999 &mdash; ЦБ (Эмиссия)<br>
     *       900 &mdash; Банкоматы/Кассы<br>
     *       407 &mdash; Предприятия<br>
     *       403 &mdash; Магазины (Мерчанты)<br>
     *       408 &mdash; Физические лица
     *     </td>
     *     <td>Определяется на основе {@link ru.phyllosedis.platform.banking.api.dto.account.AccountType}</td>
     *   </tr>
     *   <tr>
     *     <td><b>4 &ndash; 6</b></td>
     *     <td>Валюта счета (Код ISO 4217)</td>
     *     <td>
     *       810 &mdash; RUB<br>
     *       840 &mdash; USD<br>
     *       978 &mdash; EUR<br>
     *       156 &mdash; CNY
     *     </td>
     *     <td>Определяется на основе {@link ru.phyllosedis.platform.banking.api.dto.Currency}</td>
     *   </tr>
     *   <tr>
     *     <td><b>7 &ndash; 12</b></td>
     *     <td>Порядковый номер / Уникальный хвост</td>
     *     <td>Цифровой диапазон от 000000 до 999999</td>
     *     <td>Вычисляется динамически на базе текущего таймстампа и случайного числа</td>
     *   </tr>
     * </table>
     *
     * <p><b>Примеры сгенерированных номеров:</b></p>
     * <ul>
     *   <li>{@code 999810000000} &mdash; Эмиссионный счет Цифрового Бога в рублях</li>
     *   <li>{@code 408810123456} &mdash; Рублевый счет физического лица</li>
     *   <li>{@code 407840654321} &mdash; Долларовый счет предприятия</li>
     * </ul>
     */
    @PrePersist
    public void prePersist() {
        if (this.accountNumber == null || this.accountNumber.isEmpty()) {
            String typeCode = switch (type) {
                case SYSTEM_CB_EMISSION -> "999";
                case SYSTEM_BANK_CASH -> "900";
                case ENTERPRISE -> "407";
                case MERCHANT -> "403";
                case INDIVIDUAL -> "408";
            };

            String currencyCode = switch (currency) {
                case RUB -> "810";
                case USD -> "840";
                case EUR -> "978";
                case CNY -> "156";
            };

            String uniqueTail = String.format("%06d", (System.currentTimeMillis() + (int) (Math.random() * 10000)) % 1000000);

            this.accountNumber = typeCode + currencyCode + uniqueTail;
        }
        if (this.amount == null) {
            this.amount = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = AccountStatus.ACTIVE;
        }
    }
}
