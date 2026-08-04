package pl.cryptotax.infrastructure.nbp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NbpRateDto(String no, LocalDate effectiveDate, BigDecimal mid)  {
}
