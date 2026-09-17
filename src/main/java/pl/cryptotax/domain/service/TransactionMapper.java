package pl.cryptotax.domain.service;

import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.RawTransactionRow;

import java.util.Optional;
import java.util.UUID;

public class TransactionMapper {
    private final CryptoPairParser pairParser;

    public TransactionMapper(CryptoPairParser pairParser) {
        this.pairParser = pairParser;
    }

    public Optional<CryptoTransaction> map(RawTransactionRow rawRow, UUID userId) {
        if (rawRow == null) {
            return Optional.empty();
        }

        return pairParser.parse(rawRow.tradingPair())
                .map(pair -> {
                    var fiatAmount = rawRow.cryptoAmount().multiply(rawRow.fiatRate());
                    return new CryptoTransaction(
                            UUID.randomUUID(),           // 1. transactionId
                            userId,                      // 2. userId
                            pair.cryptoSymbol(),         // 3. cryptoSymbol
                            pair.fiatCurrency(),         // 4. fiatCurrency
                            rawRow.cryptoAmount(),       // 5. cryptoAmount
                            rawRow.fiatRate(),           // 6. fiatRate
                            fiatAmount,                  // 7. fiatAmount
                            rawRow.fee(),                // 8. fee
                            rawRow.transactionType(),    // 9. transactionType
                            rawRow.transactionDate()     // 10. transactionDate
                    );
                });
    }
}
