package pl.cryptotax.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.RawTransactionRow;
import pl.cryptotax.domain.model.TransactionType;
import pl.cryptotax.domain.port.TransactionFileParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class TransactionImportServiceTest {
    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionFileParser transactionFileParser;

    private TransactionImportService transactionImportService;

    @BeforeEach
    void setUp() {
        transactionImportService = new TransactionImportService(List.of(transactionFileParser), transactionMapper);
    }

    @Test
    void shouldImportTransactionsSuccessfully(){
        // Arrange
        String fileName = "transactions.csv";
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        RawTransactionRow rawTransactionRow = new RawTransactionRow("", new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), TransactionType.BUY, Instant.now());
        CryptoTransaction cryptoTransaction = new CryptoTransaction(UUID.randomUUID(), "BTC", new BigDecimal(1), new BigDecimal(2), new BigDecimal(2), "USD", TransactionType.BUY, Instant.now());

        Mockito.when(transactionFileParser.canParse(fileName)).thenReturn(true);
        Mockito.when(transactionFileParser.parse(inputStream)).thenReturn(List.of(rawTransactionRow));
        Mockito.when(transactionMapper.map(rawTransactionRow)).thenReturn(Optional.of(cryptoTransaction));
        // Act
        List<CryptoTransaction> result = transactionImportService.importTransactions(fileName, inputStream);
        // Assert
        assertThat(result).hasSize(1).containsExactly(cryptoTransaction);
    }

    @Test
    void shouldThrowExceptionWhenNoParserSupportsFile(){
        // Arrange
        String fileName = "transactions.txt";
        Mockito.when(transactionFileParser.canParse(fileName)).thenReturn(false);
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        // Act & Assert
        assertThatThrownBy(() -> transactionImportService.importTransactions(fileName, inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported file format");

    }
}
