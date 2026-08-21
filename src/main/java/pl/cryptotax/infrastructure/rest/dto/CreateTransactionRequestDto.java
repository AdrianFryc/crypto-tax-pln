package pl.cryptotax.infrastructure.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTransactionRequestDto(
        @NotEmpty
        @Size(max = 12)
        String cryptoSymbol,
        @NotNull
        @Positive
        BigDecimal cryptoAmount,
        @NotNull
        @Positive
        BigDecimal price,
        BigDecimal fee,
        @NotEmpty
        String fiatCurrency,
        @NotNull
        TransactionType transactionType,
        @NotNull
        Instant transactionDate

) {
}
