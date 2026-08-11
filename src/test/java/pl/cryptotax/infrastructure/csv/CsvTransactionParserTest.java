package pl.cryptotax.infrastructure.csv;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import pl.cryptotax.domain.model.TransactionType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvTransactionParserTest {

    private CsvTransactionParser parser;
    private InputStream inputStream;

    private static final String csvContent = """
        tradingPair,amount,price,fiatRate,transactionType,transactionDate
        BTC/USD,0.5,40000.00,40000.00,BUY,2024-01-15T10:15:30Z
        """;

    private static final String csvContentHeaderOnly = """
        tradingPair,amount,price,fiatRate,transactionType,transactionDate
        """;

    @BeforeEach
    void setUp() {
        parser = new CsvTransactionParser();


    }



    @Test
    void shouldParseValidCsvStream(){
        inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        var result = parser.parse(inputStream);

        assertThat(result).hasSize(1);

        var transaction = result.get(0);

        assertThat(transaction.tradingPair()).isEqualTo("BTC/USD");
        assertThat(transaction.cryptoAmount()).isEqualByComparingTo(new BigDecimal("0.5"));
        assertThat(transaction.fiatRate()).isEqualByComparingTo(new BigDecimal("40000.00"));
        assertThat(transaction.fiatAmount()).isEqualByComparingTo(new BigDecimal("40000.00"));
        assertThat(transaction.transactionType()).isEqualTo(TransactionType.BUY);
        assertThat(transaction.transactionDate()).isEqualTo(Instant.parse("2024-01-15T10:15:30Z"));

    }

    @Test
    void shouldReturnTrueForCsvExtension(){
        var resultCsv = parser.canParse("file.csv");
        var resultPdf = parser.canParse("file.pdf");
        var resultCsvUpper = parser.canParse("FILE.CSV");
        var resultNoExtension = parser.canParse("file");
        var resultNull = parser.canParse(null);
        var resultEmpty = parser.canParse("");

        assertThat(resultCsv).isTrue();
        assertThat(resultPdf).isFalse();
        assertThat(resultCsvUpper).isTrue();
        assertThat(resultNoExtension).isFalse();
        assertThat(resultNull).isFalse();
        assertThat(resultEmpty).isFalse();

    }

    @Test
    void shouldReturnEmptyListWhenInputStreamIsWithHeaderOnly() {
        inputStream = new ByteArrayInputStream(csvContentHeaderOnly.getBytes(StandardCharsets.UTF_8));
        var result = parser.parse(inputStream);

        assertThat(result).isEmpty();
    }

}
