package pl.cryptotax.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CryptoTransaction(UUID transactionId,
                                String cryptoSymbol,
                                BigDecimal cryptoAmount,
                                BigDecimal fiatRate,
                                BigDecimal fiatAmount,
                                String fiatCurrency,
                                TransactionType transactionType,
                                Instant transactionDate) {
    public CryptoTransaction {
        if (cryptoAmount != null && cryptoAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Crypto amount must be greater than zero");
        }
    }
}
