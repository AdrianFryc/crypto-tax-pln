package pl.cryptotax.infrastructure.nbp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.cryptotax.domain.model.ExchangeRate;
import pl.cryptotax.domain.service.NbpClient;
import pl.cryptotax.infrastructure.nbp.dto.NbpResponse;
import pl.cryptotax.infrastructure.nbp.exception.NbpClientException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

@Component
@Slf4j
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
                log.debug("Sending GET request to NBP API: {}", uri);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    NbpResponse dto = objectMapper.readValue(response.body(), NbpResponse.class);
                    BigDecimal rate = dto.rates().getFirst().mid();
                    log.info("Successfully fetched NBP rate for currency {} on date {}: {}", currency, currentDate, rate);

                    return new ExchangeRate(currency, dto.rates().getFirst().mid(), currentDate);
                } else if (response.statusCode() == 404) {
                    log.warn("Rate not found for currency {} on date {}. Retrying previous day...", currency, currentDate);
                    currentDate = currentDate.minusDays(1);
                    attempts++;
                } else {
                    log.error("Unexpected NBP API status code: {} for URI: {}", response.statusCode(), uri);
                    throw new NbpClientException("Unexpected NBP API status code: " + response.statusCode());
                }
            } catch (IOException e) {
                log.error("Communication or JSON mapping error with NBP API for currency {}", currency, e);
                throw new NbpClientException("Error during communication or JSON mapping with NBP API", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread was interrupted while waiting for NBP API response for currency {}", currency, e);
                throw new NbpClientException("Thread was interrupted while waiting for NBP API response", e);
            }
        }
        log.error("NBPClientException Could not find NBP exchange rate for {} after {} attempts before {}", currency, maxAttempts, effectiveDate);
        throw new NbpClientException("Could not find NBP exchange rate for " + currency + " after " + maxAttempts + " attempts before " + effectiveDate);
    }
}