package pl.cryptotax.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/*
 * transactionId: Unikalny identyfikator transakcji (UUID) generowany w systemie.

* cryptoSymbol: Kod kupowanej lub sprzedawanej kryptowaluty (np. "BTC", "ETH").

* cryptoAmount: Ilość (wolumen) kryptowaluty biorąca udział w transakcji (np. 0.5).

* fiatRate: Cena za 1 jednostkę kryptowaluty w walucie tradycyjnej w momencie transakcji.

* fiatAmount: Łączna kwota transakcji w walucie tradycyjnej (iloczyn cryptoAmount * fiatRate).

* fee: Prowizja giełdowa pobrana za realizację transakcji, wyrażona w walucie tradycyjnej (stanowi koszt KUP).

* fiatCurrency: Kod waluty tradycyjnej/fiducjarnej (np. "PLN", "USD", "EUR").

* transactionType: Typ operacji – wartość enuma (BUY dla zakupu, SELL dla sprzedaży).

* transactionDate: Dokładny Znacznik czasu wykonania transakcji w strefie UTC (Instant).
 */

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
