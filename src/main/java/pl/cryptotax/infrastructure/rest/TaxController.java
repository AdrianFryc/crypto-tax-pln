package pl.cryptotax.infrastructure.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cryptotax.application.service.TaxSummaryApplicationService;
import pl.cryptotax.domain.model.TaxSummary;
import pl.cryptotax.infrastructure.rest.dto.TaxSummaryResponseDto;

@RestController
@RequestMapping("/api/v1/tax")
public class TaxController {
    private final TaxSummaryApplicationService taxSummaryApplicationService;

    public TaxController(TaxSummaryApplicationService taxSummaryApplicationService) {
        this.taxSummaryApplicationService = taxSummaryApplicationService;
    }

    @GetMapping("/summary/{year}")
    public TaxSummaryResponseDto getTaxSummary(@PathVariable int year) {
        TaxSummary taxSummary = taxSummaryApplicationService.calculateTaxForYear(year);
        return TaxSummaryResponseDto.from(taxSummary);
    }

}
