package pl.cryptotax.infrastructure.database.mapper;

import org.springframework.stereotype.Component;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.infrastructure.database.entity.TransactionEntity;

import java.math.BigDecimal;

@Component
public class TransactionEntityMapper {

    public TransactionEntity toEntity(CryptoTransaction domain) {
        return new TransactionEntity(
                domain.cryptoSymbol(),
                domain.cryptoAmount(),
                domain.fiatRate(),
                domain.fiatAmount(),
                BigDecimal.ZERO, // Domyślna prowizja 0, jeśli CryptoTransaction jej nie ma
                domain.transactionType(),
                domain.transactionDate()
        );
    }

    public CryptoTransaction toDomain(TransactionEntity entity) {
        return new CryptoTransaction(
                entity.getId(),
                entity.getSymbol(),
                entity.getAmount(),
                entity.getPrice(),
                entity.getAmount().multiply(entity.getPrice()),
                entity.getSymbol(),
                entity.getType(),
                entity.getTimestamp()
        );
    }
}