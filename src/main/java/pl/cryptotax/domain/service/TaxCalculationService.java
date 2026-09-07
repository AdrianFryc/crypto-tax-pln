package pl.cryptotax.domain.service;

import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.TaxSummary;
import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class TaxCalculationService {
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");
    private final NbpClient nbpClient;

    public TaxCalculationService(NbpClient nbpClient) {
        this.nbpClient = nbpClient;
    }

    public TaxSummary calculateTax(List<CryptoTransaction> transactions){
        if (transactions == null || transactions.isEmpty()) {
            return new TaxSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal netProfitPln = BigDecimal.ZERO;

        for (CryptoTransaction transaction : transactions) {
            LocalDate txDate = transaction.transactionDate()
                    .atZone(WARSAW_ZONE)
                    .toLocalDate();

            // 1. Prowizja (fee) to zawsze koszt KUP dla obu typów transakcji
            BigDecimal feeInPln = convertToPln(transaction.fee(), transaction.fiatCurrency(), txDate);
            totalCost = totalCost.add(feeInPln);

            // 2. Wartość transakcji
            BigDecimal amountInPln = convertToPln(transaction.fiatAmount(), transaction.fiatCurrency(), txDate);

            if (transaction.transactionType() == TransactionType.BUY) {
                totalCost = totalCost.add(amountInPln);
            } else if (transaction.transactionType() == TransactionType.SELL) {
                totalIncome = totalIncome.add(amountInPln);
            }
        }
        netProfitPln = totalIncome.subtract(totalCost);
        return new TaxSummary(totalIncome, totalCost, netProfitPln);
    }

    private BigDecimal convertToPln(BigDecimal amount, String currency, LocalDate date) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if ("PLN".equalsIgnoreCase(currency)) {
            return amount;
        }
        BigDecimal rate = nbpClient.getExchangeRate(currency, date).rate();
        return amount.multiply(rate);
    }
}
