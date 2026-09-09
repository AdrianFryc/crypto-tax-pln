package pl.cryptotax.infrastructure.rest.dto;

import pl.cryptotax.domain.model.TaxSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TaxSummaryResponseDto(BigDecimal totalIncome, BigDecimal totalCost, BigDecimal netProfit) {
    public static TaxSummaryResponseDto from(TaxSummary domain) {
        return new TaxSummaryResponseDto(
                domain.totalIncome().setScale(2, RoundingMode.HALF_UP),
                domain.totalCost().setScale(2, RoundingMode.HALF_UP),
                domain.netProfitPln().setScale(2, RoundingMode.HALF_UP)
        );
    }
}
