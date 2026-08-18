package pl.cryptotax.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CryptoTransaction(UUID transactionId,
                                String cryptoSymbol,
                                BigDecimal cryptoAmount,
                                BigDecimal fiatRate,
                                BigDecimal fiatAmount,
                                BigDecimal fee,
                                String fiatCurrency,
                                TransactionType transactionType,
                                Instant transactionDate) {
    public CryptoTransaction {
        fee = Objects.requireNonNullElse(fee, BigDecimal.ZERO);
        if (cryptoAmount != null && cryptoAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Crypto amount must be greater than zero");
        }
    }
}
