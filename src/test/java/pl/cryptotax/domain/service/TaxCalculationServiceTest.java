package pl.cryptotax.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.ExchangeRate;
import pl.cryptotax.domain.model.TaxSummary;
import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxCalculationServiceTest {

    @Mock
    private NbpClient nbpClient;

    private TaxCalculationService taxCalculationService;

    @BeforeEach
    void setUp() {
        taxCalculationService = new TaxCalculationService(nbpClient);
    }

    @Test
    void shouldReturnZeroSummaryWhenTransactionsListIsEmptyOrNull() {
        TaxSummary resultForNull = taxCalculationService.calculateTax(null);
        TaxSummary resultForEmpty = taxCalculationService.calculateTax(Collections.emptyList());

        TaxSummary expected = new TaxSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(resultForNull).isEqualTo(expected);
        assertThat(resultForEmpty).isEqualTo(expected);
        verifyNoInteractions(nbpClient);
    }

    @Test
    void shouldCalculateTaxForPlnTransactionsWithoutCallingNbp() {
        List<CryptoTransaction> transactions = List.of(
                new CryptoTransaction(
                        UUID.randomUUID(),
                        "BTC/PLN",
                        BigDecimal.valueOf(0.5),
                        BigDecimal.valueOf(40000.00),
                        BigDecimal.valueOf(20000.00),
                        BigDecimal.valueOf(10.00),
                        "PLN",
                        TransactionType.BUY,
                        Instant.parse("2024-01-15T10:15:30Z")
                ),
                new CryptoTransaction(
                        UUID.randomUUID(),
                        "BTC/PLN",
                        BigDecimal.valueOf(0.1),
                        BigDecimal.valueOf(30000.00),
                        BigDecimal.valueOf(3000.00),
                        BigDecimal.valueOf(2.00),
                        "PLN",
                        TransactionType.SELL,
                        Instant.parse("2024-01-17T10:15:30Z")
                )
        );

        TaxSummary result = taxCalculationService.calculateTax(transactions);

        // totalIncome = 3000.00
        // totalCost = 20000.00 (BUY) + 10.00 (BUY fee) + 2.00 (SELL fee) = 20012.00
        // netProfitPln = 3000.00 - 20012.00 = -17012.00
        assertThat(result.totalIncome()).isEqualByComparingTo("3000.00");
        assertThat(result.totalCost()).isEqualByComparingTo("20012.00");
        assertThat(result.netProfitPln()).isEqualByComparingTo("-17012.00");

        verifyNoInteractions(nbpClient);
    }

    @Test
    void shouldCalculateTaxForForeignCurrencyWithFeesAndNbpRates() {
        when(nbpClient.getExchangeRate(eq("USD"), any()))
                .thenReturn(new ExchangeRate("USD", new BigDecimal("4.00"), LocalDate.of(2024, 1, 14)));

        List<CryptoTransaction> transactions = List.of(
                new CryptoTransaction(
                        UUID.randomUUID(),
                        "BTC/USD",
                        BigDecimal.valueOf(1.0),
                        BigDecimal.valueOf(100.00),
                        BigDecimal.valueOf(100.00), // 100 USD * 4.00 = 400 PLN
                        BigDecimal.valueOf(2.00),   // 2 USD * 4.00 = 8 PLN
                        "USD",
                        TransactionType.BUY,
                        Instant.parse("2024-01-15T10:15:30Z")
                ),
                new CryptoTransaction(
                        UUID.randomUUID(),
                        "BTC/USD",
                        BigDecimal.valueOf(1.0),
                        BigDecimal.valueOf(200.00),
                        BigDecimal.valueOf(200.00), // 200 USD * 4.00 = 800 PLN
                        BigDecimal.valueOf(5.00),   // 5 USD * 4.00 = 20 PLN
                        "USD",
                        TransactionType.SELL,
                        Instant.parse("2024-01-17T10:15:30Z")
                )
        );

        TaxSummary result = taxCalculationService.calculateTax(transactions);

        // totalIncome = 200 USD * 4.00 = 800 PLN
        // totalCost = (100 USD * 4.00) + (2 USD * 4.00) + (5 USD * 4.00) = 400 + 8 + 20 = 428 PLN
        // netProfitPln = 800 - 428 = 372 PLN
        assertThat(result.totalIncome()).isEqualByComparingTo("800.00");
        assertThat(result.totalCost()).isEqualByComparingTo("428.00");
        assertThat(result.netProfitPln()).isEqualByComparingTo("372.00");
    }
}