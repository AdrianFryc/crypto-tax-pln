package pl.cryptotax.infrastructure.rest.dto;

import pl.cryptotax.domain.model.TaxSummary;

import java.math.BigDecimal;

public record TaxSummaryResponseDto(BigDecimal totalIncome, BigDecimal totalCost, BigDecimal netProfit) {
    public static TaxSummaryResponseDto from(TaxSummary domain) {
        return new TaxSummaryResponseDto(
                domain.totalIncome(),
                domain.totalCost(),
                domain.netProfitPln()
        );
    }
}
