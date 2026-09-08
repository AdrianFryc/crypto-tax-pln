package pl.cryptotax.domain.port;

import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.infrastructure.database.entity.TransactionEntity;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository {

    CryptoTransaction save(CryptoTransaction transaction);
    List<CryptoTransaction> saveAll(List<CryptoTransaction> transactions);
    List<CryptoTransaction> findAll();
    List<CryptoTransaction> findAllByTransactionDateBetween(Instant start, Instant end);
}
