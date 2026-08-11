package pl.cryptotax.infrastructure.nbp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cryptotax.domain.model.ExchangeRate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NbpHttpClientTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @Mock private HttpResponse<String> response404;

    @Mock private HttpResponse<String> response200;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private NbpHttpClient nbpHttpClient;

    @BeforeEach
    void setUp() {
        // Inicjalizujemy NbpHttpClient z mockiem HttpClient, prawdziwym ObjectMapperem oraz przykładową konfiguracją (maxAttempts = 3)
        nbpHttpClient = new NbpHttpClient(
                httpClient,
                objectMapper,
                3,
                "https://api.nbp.pl/api/exchangerates/rates/a/{currency}/{date}/?format=json"
        );
    }

    @Test
    void shouldReturnExchangeRateWhenNbpReturns200() throws IOException, InterruptedException {
        // 1. ARRANGE (Przygotowanie atrap i danych wejściowych)
        // a) Ustaw statusCode na 200 na obiekcie httpResponse
        // b) Ustaw body na prawidłowy string JSON na obiekcie httpResponse
        // c) Ustaw httpClient.send(any(HttpRequest.class), any()) tak, aby zwracał httpResponse

        Mockito.when(httpResponse.statusCode()).thenReturn(200);
        Mockito.when(httpResponse.body()).thenReturn("{\"rates\":[{\"mid\":4.3210}]}");
        Mockito.when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // 2. ACT (Wykonanie testowanej metody)
        // Wywołaj prawdziwą metodę: ExchangeRate rate = nbpHttpClient.getExchangeRate("USD", LocalDate.of(2024, 3, 9));
        ExchangeRate rate = nbpHttpClient.getExchangeRate("USD", LocalDate.of(2024, 3, 9));

        // 3. ASSERT (Sprawdzenie oczekiwanych rezultatów)
        assertEquals(new BigDecimal("4.3210"), rate.rate());
        assertEquals("USD", rate.currency());
        assertEquals(LocalDate.of(2024, 3, 8), rate.effectiveDate());

    }

    @Test
    void shouldRetryAndReturnRateWhenFirstAttemptIs404() throws IOException, InterruptedException {
        // 1. ARRANGE (Przygotowanie scenografii PRZED uruchomieniem metody)

        // a) Przygotuj mock response404
        Mockito.when(response404.statusCode()).thenReturn(404);

        // b) Przygotuj mock response200 (status + body!)
        Mockito.when(response200.statusCode()).thenReturn(200);
        Mockito.when(response200.body()).thenReturn("{\"rates\":[{\"mid\":4.3210}]}");

        // c) Powiedz httpClient, co ma zwracać przy KOLEJNYCH wywołaniach wewnątrz pętli while:
        Mockito.when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response404, response200);

        // 2. ACT (Jednorazowe wywołanie testowanej logiki)
        ExchangeRate rate = nbpHttpClient.getExchangeRate("USD", LocalDate.of(2024, 3, 10));

        // 3. ASSERT (Sprawdzenie czy po ponowieniu dostaliśmy poprawny kurs i datę)
        assertEquals(LocalDate.of(2024, 3, 8), rate.effectiveDate());
        assertEquals(new BigDecimal("4.3210"), rate.rate());

        verify(httpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}