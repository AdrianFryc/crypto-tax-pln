package pl.cryptotax.infrastructure.database.mapper;

import org.springframework.stereotype.Component;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.infrastructure.database.entity.TransactionEntity;
import pl.cryptotax.infrastructure.database.entity.UserEntity;


@Component
public class TransactionEntityMapper {

    public TransactionEntity toEntity(CryptoTransaction domain) {
        return new TransactionEntity(
                domain.transactionId(),
                UserEntity.ofId(domain.userId()),
                domain.cryptoSymbol(),
                domain.fiatCurrency(),
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
                entity.getUser().getId(),
                entity.getCryptoSymbol(),
                entity.getFiatCurrency(),
                entity.getAmount(),
                entity.getPrice(),
                entity.getAmount().multiply(entity.getPrice()),
                entity.getFee(),
                entity.getType(),
                entity.getTimestamp()
        );
    }
}