package pl.cryptotax.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record RawTransactionRow(String tradingPair,
                                BigDecimal cryptoAmount,    // ilość kryptowaluty
                                BigDecimal fiatRate,    // cena kryptowaluty
                                BigDecimal fiatAmount,  // wydana kwota w walucie
                                BigDecimal fee, // kosz transakcji(prowizja)
                                TransactionType transactionType,    // typ transakcji
                                Instant transactionDate) {
    public RawTransactionRow {
        fee = (fee != null) ? fee : BigDecimal.ZERO;
    }
}
