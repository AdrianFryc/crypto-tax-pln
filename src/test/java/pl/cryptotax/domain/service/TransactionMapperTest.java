package pl.cryptotax.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.ParsedPair;
import pl.cryptotax.domain.model.RawTransactionRow;
import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionMapperTest {

    @InjectMocks
    private TransactionMapper transactionMapper;

    @Mock
    private CryptoPairParser pairParser;

    @Test
    void shouldMapRawTransactionRowToCryptoTransaction() {
        // given
        Instant now = Instant.now();
        RawTransactionRow rawTransactionRow = new RawTransactionRow("BTC/USD", new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("2"),TransactionType.BUY, now);

        // Mokujemy TYLKO zależność (pairParser)
        Mockito.when(pairParser.parse("BTC/USD"))
                .thenReturn(Optional.of(new ParsedPair("BTC", "USD")));

        // WHEN: Wywołanie PRAWDZIWEJ metody testowanej i zapisanie wyniku
        Optional<CryptoTransaction> result = transactionMapper.map(rawTransactionRow);

        // THEN: Asercje
        assertThat(result).isPresent();

        CryptoTransaction tx = result.get();

        // 1. Unikalny identyfikator UUID sprawdzamy pod kątem braku null-a
        assertThat(tx.transactionId()).isNotNull();

        // 2. Symbole i typy proste sprawdzamy przez isEqualTo
        assertThat(tx.cryptoSymbol()).isEqualTo("BTC");
        assertThat(tx.fiatCurrency()).isEqualTo("USD");
        assertThat(tx.transactionType()).isEqualTo(TransactionType.BUY);
        assertThat(tx.transactionDate()).isEqualTo(now);

        // 3. Kwoty BigDecimal sprawdzamy przez isEqualByComparingTo (ignoruje różnice w skali np. 1 vs 1.00)
        assertThat(tx.fiatRate()).isEqualByComparingTo("2.0");
        assertThat(tx.fiatAmount()).isEqualByComparingTo("2.00");
    }

    @Test
    void shouldReturnEmptyWhenPairParsingFails(){
        // given
        Instant now = Instant.now();
        RawTransactionRow rawTransactionRow = new RawTransactionRow("BTC/USDD", new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("2"),TransactionType.BUY, now);

        Mockito.when(pairParser.parse("BTC/USDD")).thenReturn(Optional.empty());

        Optional<CryptoTransaction> result = transactionMapper.map(rawTransactionRow);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenRawRowIsNull(){
        // given
        Optional<CryptoTransaction> result = transactionMapper.map(null);

        // then
        assertThat(result).isEmpty();
    }
}
