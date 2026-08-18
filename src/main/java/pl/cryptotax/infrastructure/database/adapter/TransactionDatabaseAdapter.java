package pl.cryptotax.infrastructure.database.adapter;

import org.springframework.stereotype.Repository;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.port.TransactionRepository;
import pl.cryptotax.infrastructure.database.entity.TransactionEntity;
import pl.cryptotax.infrastructure.database.mapper.TransactionEntityMapper;
import pl.cryptotax.infrastructure.database.repository.TransactionJpaRepository;

import java.util.List;

@Repository
public class TransactionDatabaseAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionEntityMapper entityMapper;

    public TransactionDatabaseAdapter(TransactionJpaRepository jpaRepository,
                                      TransactionEntityMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public CryptoTransaction save(CryptoTransaction transaction) {
        TransactionEntity entity = entityMapper.toEntity(transaction);
        TransactionEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public List<CryptoTransaction> saveAll(List<CryptoTransaction> transactions) {
        // 1. Zamiana całej listy z Domeny na Encje
        List<TransactionEntity> entities = transactions.stream()
                .map(entityMapper::toEntity)
                .toList();

        // 2. Zbiorczy zapis w Spring Data JPA
        List<TransactionEntity> savedEntities = jpaRepository.saveAll(entities);

        // 3. Zamiana zapisanych Encji z powrotem na Domenę
        return savedEntities.stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}