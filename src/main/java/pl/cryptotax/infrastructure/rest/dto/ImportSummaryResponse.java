package pl.cryptotax.infrastructure.rest.dto;

import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public record ImportSummaryResponse(int totalTransactions,
                                    BigDecimal totalFiatCost,
                                    BigDecimal totalFiatRevenue,
                                    BigDecimal netProfit,
                                    List<TransactionItemResponse> transactions) {
    public static ImportSummaryResponse from(List<CryptoTransaction> transactions){
        if(transactions == null || transactions.isEmpty()){
            return new ImportSummaryResponse(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        }

        BigDecimal totalFiatCost = transactions.stream()
                .filter(transaction -> transaction.transactionType() == TransactionType.BUY)
                .map(CryptoTransaction::fiatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFiatRevenue = transactions.stream()
                .filter(transaction -> transaction.transactionType() == TransactionType.SELL)
                .map(CryptoTransaction::fiatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netProfit = totalFiatRevenue.subtract(totalFiatCost);

        List<TransactionItemResponse> transactionList = transactions.stream()
                .map(TransactionItemResponse::fromDomain)
                .toList();

        return new ImportSummaryResponse(transactions.size(), totalFiatCost, totalFiatRevenue, netProfit, transactionList);
    }
}
