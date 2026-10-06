package ru.phyllosedis.platform.banking.impl.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.phyllosedis.platform.banking.api.dto.Currency;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Курс прейскуранта: сколько {@code quote} дают за 1 {@code base}.
 * Обратная пара считается инверсией 1/rate, если прямой записи нет.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"exchange_rate\"")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "base_currency", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private Currency baseCurrency;

    @Column(name = "quote_currency", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private Currency quoteCurrency;

    @Column(name = "rate", precision = 20, scale = 8, nullable = false)
    private BigDecimal rate;

    @Column(name = "valid_from", nullable = false)
    private ZonedDateTime validFrom;
}
