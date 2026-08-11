package pl.cryptotax.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record RawTransactionRow(String tradingPair,
                                BigDecimal cryptoAmount,
                                BigDecimal fiatRate,
                                BigDecimal fiatAmount,
                                TransactionType transactionType,
                                Instant transactionDate) {
}
