package pl.cryptotax.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.cryptotax.domain.model.ParsedPair;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoPairParserTest {

    private CryptoPairParser parser;

    @BeforeEach
    void setUp() {
        parser = new CryptoPairParser();
    }

    @Test
    void shouldParsePair() {
        // given
        String rawPair = "BTC/USD";

        // when

        Optional<ParsedPair> result = parser.parse(rawPair);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().cryptoSymbol()).isEqualTo("BTC");
        assertThat(result.get().fiatCurrency()).isEqualTo("USD");

        // lub zamiast dwóch powyższych testów jedno wystarczy
        assertThat(result).contains(new ParsedPair("BTC", "USD"));
    }

    @Test
    void shouldRejectCryptoCryptoPairWithStablecoin(){
        String rawPair = "BTC/USDT";

        Optional<ParsedPair> result = parser.parse(rawPair);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldRejectFiatFiatPair(){
        String rawPair = "USD/PLN";

        Optional<ParsedPair> result = parser.parse(rawPair);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleNullAndBlankInput(){
        Optional<ParsedPair> result = parser.parse(null);
        assertThat(result).isNotPresent();

        result = parser.parse("");
        assertThat(result).isEmpty();
    }
}
