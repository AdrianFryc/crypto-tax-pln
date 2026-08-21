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
import pl.cryptotax.domain.port.TransactionRepository;
import pl.cryptotax.domain.service.TransactionImportService;
import pl.cryptotax.infrastructure.rest.dto.TransactionResponseDto;
import pl.cryptotax.infrastructure.rest.mapper.TransactionRestMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionImportService transactionImportService;

    @MockitoBean
    private TransactionRestMapper transactionRestMapper;

    @MockitoBean
    private TransactionRepository transactionRepository;

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
                UUID.randomUUID(), "BTC", BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TWO, null,"PLN", TransactionType.BUY, Instant.now()
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

    @Test
    public void shouldCreateTransactionAndReturn201Created() throws Exception {
        // given
        String requestJson = """
            {
                "cryptoSymbol": "BTC",
                "cryptoAmount": 1.0,
                "price": 50000.0,
                "fee": 10.0,
                "fiatCurrency": "PLN",
                "transactionType": "BUY",
                "transactionDate": "2026-08-21T12:00:00Z"
            }
            """;

        UUID generatedId = UUID.randomUUID();
        Instant now = Instant.parse("2026-08-21T12:00:00Z");

        CryptoTransaction mockDomain = new CryptoTransaction(
                generatedId, "BTC", BigDecimal.ONE, BigDecimal.valueOf(50000.0),
                BigDecimal.valueOf(50000.0), BigDecimal.valueOf(10.0), "PLN",
                TransactionType.BUY, now
        );

        TransactionResponseDto mockResponseDto = new TransactionResponseDto(
                generatedId, "BTC", BigDecimal.ONE, BigDecimal.valueOf(50000.0),
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50000.0), "PLN",
                TransactionType.BUY, now
        );

        when(transactionRestMapper.toDomain(any())).thenReturn(mockDomain);
        when(transactionRepository.save(any())).thenReturn(mockDomain);
        when(transactionRestMapper.toDto(any())).thenReturn(mockResponseDto);

        // when & then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(generatedId.toString()))
                .andExpect(jsonPath("$.cryptoSymbol").value("BTC"))
                .andExpect(jsonPath("$.cryptoAmount").value(1))
                .andExpect(jsonPath("$.fiatAmount").value(50000.0))
                .andExpect(jsonPath("$.transactionType").value("BUY"));
    }
}
