package pl.cryptotax.infrastructure.nbp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.cryptotax.domain.model.ExchangeRate;
import pl.cryptotax.domain.service.NbpClient;
import pl.cryptotax.infrastructure.nbp.dto.NbpResponse;
import pl.cryptotax.infrastructure.nbp.exception.NbpClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

@Component
public class NbpHttpClient implements NbpClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final int maxAttempts;
    private final String urlTemplate;

    public NbpHttpClient(HttpClient httpClient, ObjectMapper objectMapper, @Value("${nbp.api.max-attempts}") int maxAttempts, @Value("${nbp.api.url-template}")String urlTemplate) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.maxAttempts = maxAttempts;
        this.urlTemplate = urlTemplate;
    }

    @Override
    public ExchangeRate getExchangeRate(String currency, LocalDate effectiveDate) {
        LocalDate currentDate = effectiveDate.minusDays(1);
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
                        .buildAndExpand(currency, currentDate)
                        .toUri();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    NbpResponse dto = objectMapper.readValue(response.body(), NbpResponse.class);
                    return new ExchangeRate(currency, dto.rates().get(0).mid(), currentDate);
                } else if (response.statusCode() == 404) {
                    currentDate = currentDate.minusDays(1);
                    attempts++;
                } else {
                    throw new NbpClientException("Unexpected NBP API status code: " + response.statusCode());
                }
            } catch (Exception e) {
                throw new NbpClientException("Error during communication with NBP API", e);
            }
        }

        throw new IllegalStateException("Could not find NBP exchange rate for " + currency + " after " + maxAttempts + " attempts before " + effectiveDate);
    }
}