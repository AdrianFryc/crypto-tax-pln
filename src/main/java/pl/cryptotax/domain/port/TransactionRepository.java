package pl.cryptotax.domain.port;

import pl.cryptotax.domain.model.CryptoTransaction;

import java.util.List;

public interface TransactionRepository {

    CryptoTransaction save(CryptoTransaction transaction);
    List<CryptoTransaction> saveAll(List<CryptoTransaction> transactions);
    List<CryptoTransaction> findAll();
}
