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

    public Optional<CryptoTransaction> map(RawTransactionRow rawRow) {
        if (rawRow == null) {
            return Optional.empty();
        }

        return pairParser.parse(rawRow.tradingPair())
                .map(pair -> {
                    var fiatAmount = rawRow.cryptoAmount().multiply(rawRow.fiatRate());
                    return new CryptoTransaction(
                            UUID.randomUUID(),           // 1. transactionId
                            pair.cryptoSymbol(),          // 2. cryptoSymbol (akcesor z ())
                            rawRow.cryptoAmount(),              // 3. cryptoAmount
                            rawRow.fiatRate(),
                            fiatAmount,                   // 4. fiatAmount
                            pair.fiatCurrency(),          // 5. fiatCurrency (akcesor z ())
                            rawRow.transactionType(),     // 6. transactionType
                            rawRow.transactionDate()      // 7. transactionDate
                    );
                });
    }
}
