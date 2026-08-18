package pl.cryptotax.infrastructure.database.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.TransactionType;
import pl.cryptotax.infrastructure.database.entity.TransactionEntity;
import pl.cryptotax.infrastructure.database.mapper.TransactionEntityMapper;
import pl.cryptotax.infrastructure.database.repository.TransactionJpaRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TransactionDatabaseAdapter.class, TransactionEntityMapper.class})
public class TransactionDatabaseAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TransactionDatabaseAdapter adapter;

    @Autowired
    private TransactionJpaRepository jpaRepository;

    @Test
    void shouldSaveAndRetrieveTransactionsInRealPostgres() {
        CryptoTransaction cryptoTransaction = new CryptoTransaction(UUID.randomUUID(),
                "BTC",
                new BigDecimal("0.5"),
                new BigDecimal("60000.00"),
                new BigDecimal("10.00"),
                null,
                "PLN",
                TransactionType.BUY,
                Instant.now()
        );

        adapter.saveAll(List.of(cryptoTransaction));

        List<CryptoTransaction> transactions = adapter.findAll();

        assertThat(transactions).hasSize(1);
        assertThat(transactions.getFirst().cryptoSymbol()).isEqualTo("BTC");
        assertThat(transactions.getFirst().transactionId()).isEqualTo(cryptoTransaction.transactionId());
    }
}