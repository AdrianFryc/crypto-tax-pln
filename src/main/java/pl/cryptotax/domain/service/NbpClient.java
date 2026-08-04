package pl.cryptotax.domain.service;

import pl.cryptotax.domain.model.ExchangeRate;

import java.time.LocalDate;

public interface NbpClient {
    ExchangeRate getExchangeRate(String currency, LocalDate effectiveDate);
}
