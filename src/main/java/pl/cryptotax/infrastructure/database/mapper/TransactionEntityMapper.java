package pl.cryptotax.infrastructure.database.mapper;

import org.springframework.stereotype.Component;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.infrastructure.database.entity.TransactionEntity;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class TransactionEntityMapper {

    public TransactionEntity toEntity(CryptoTransaction domain) {
        return new TransactionEntity(
                domain.transactionId(),
                domain.cryptoSymbol(),
                domain.cryptoAmount(),
                domain.fiatRate(),
                domain.fiatAmount(),
                domain.fee(),
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
                entity.getFee(),
                entity.getSymbol(),
                entity.getType(),
                entity.getTimestamp()
        );
    }
}