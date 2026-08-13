package pl.cryptotax.infrastructure.rest;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.TransactionType;
import pl.cryptotax.domain.service.TransactionImportService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionImportService transactionImportService;

    @Test
    public void shouldImportTransactionsAndReturn200Ok() throws Exception {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                "dummy content".getBytes()
        );

        CryptoTransaction cryptoTransaction = new CryptoTransaction(
                UUID.randomUUID(), "BTC", BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TWO, "PLN", TransactionType.BUY, Instant.now()
        );

        Mockito.when(transactionImportService.importTransactions(eq("transactions.csv"), any()))
                .thenReturn(List.of(cryptoTransaction));

        // when & then
        mockMvc.perform(multipart("/api/v1/transactions/import").file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransactions").value(1))
                .andExpect(jsonPath("$.netProfit").value(-2))
                .andExpect(jsonPath("$.transactions[0].pair").value("BTC"));
    }
}
