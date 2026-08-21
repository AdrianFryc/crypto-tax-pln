package pl.cryptotax.infrastructure.rest.dto;

import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponseDto(
        UUID transactionId,
        String cryptoSymbol,
        BigDecimal cryptoAmount,
        BigDecimal price,
        BigDecimal fee,
        BigDecimal fiatAmount,
        String fiatCurrency,
        TransactionType transactionType,
        Instant transactionDate
) {
}
