package pl.cryptotax.application.service;

import org.springframework.stereotype.Service;
import pl.cryptotax.domain.exception.InvalidTaxYearException;
import pl.cryptotax.domain.model.TaxSummary;
import pl.cryptotax.domain.port.TransactionRepository;
import pl.cryptotax.domain.service.TaxCalculationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneId;

@Service
public class TaxSummaryApplicationService {
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    private final TaxCalculationService taxCalculationService;
    private final TransactionRepository transactionRepository;

    public TaxSummaryApplicationService(TaxCalculationService taxCalculationService, TransactionRepository transactionRepository) {
        this.taxCalculationService = taxCalculationService;
        this.transactionRepository = transactionRepository;
    }

    public TaxSummary calculateTaxForYear(int year){
        if(year < 2015)
            throw new InvalidTaxYearException("Year cannot be lower than 2015");
        if(year > Year.now().getValue())
            throw new InvalidTaxYearException("Year cannot be higher than " + Year.now().getValue());
        var start = LocalDate.of(year, 1, 1).atStartOfDay(WARSAW_ZONE).toInstant();
        var end = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX).atZone(WARSAW_ZONE).toInstant();
        var transactions = transactionRepository.findAllByTransactionDateBetween(start, end);
        return taxCalculationService.calculateTax(transactions);
    }
}
