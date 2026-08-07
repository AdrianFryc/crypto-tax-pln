package pl.cryptotax.domain.model;

import java.math.BigDecimal;

public record TaxSummary(BigDecimal totalIncome, BigDecimal totalCost, BigDecimal netProfitPln) {
}
