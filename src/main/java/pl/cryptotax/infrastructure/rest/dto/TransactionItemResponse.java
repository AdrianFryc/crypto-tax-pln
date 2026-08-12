package pl.cryptotax.infrastructure.rest.dto;

import pl.cryptotax.domain.model.CryptoTransaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionItemResponse(String pair, BigDecimal cryptoAmount, BigDecimal fiatAmount, String type, Instant date) {
    public static TransactionItemResponse fromDomain(CryptoTransaction domain) {
        return  new TransactionItemResponse(domain.cryptoSymbol(), domain.cryptoAmount(), domain.fiatAmount(), domain.transactionType().name(), domain.transactionDate());
    }
}
