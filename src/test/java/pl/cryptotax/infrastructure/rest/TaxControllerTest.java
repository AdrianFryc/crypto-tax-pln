package pl.cryptotax.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.cryptotax.application.service.TaxSummaryApplicationService;
import pl.cryptotax.domain.exception.InvalidTaxYearException;
import pl.cryptotax.domain.model.TaxSummary;
import pl.cryptotax.infrastructure.rest.exception.GlobalExceptionHandler;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaxController.class)
@Import(GlobalExceptionHandler.class)
public class TaxControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxSummaryApplicationService taxSummaryApplicationService;

    @Test
    public void shouldReturnTaxSummaryWhenYearIsValid() throws Exception {
        // given
        var taxSummary = new TaxSummary(new BigDecimal("1000"), new BigDecimal("500"), new BigDecimal("500"));

        Mockito.when(taxSummaryApplicationService.calculateTaxForYear(2024))
                .thenReturn(taxSummary);

        // when
        mockMvc.perform(get("/api/v1/tax/summary/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(1000))
                .andExpect(jsonPath("$.totalCost").value(500))
                .andExpect(jsonPath("$.netProfit").value(500));
    }

    @Test
    public void shouldReturn400BadRequestWhenYearIsInvalid() throws Exception{
        Mockito.when(taxSummaryApplicationService.calculateTaxForYear(2010))
                .thenThrow(new InvalidTaxYearException("Year cannot be lower than 2015"));

        // when
        mockMvc.perform(get("/api/v1/tax/summary/2010"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Year cannot be lower than 2015"));
    }
}
