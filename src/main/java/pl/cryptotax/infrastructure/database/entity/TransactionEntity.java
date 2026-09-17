package pl.cryptotax.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Id
    private UUID id;

    @Column(nullable = false)
    private String cryptoSymbol;

    @Column(nullable = false)
    private String fiatCurrency;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal amount;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal price;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal fiatRate;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal fee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private Instant timestamp;

    protected TransactionEntity() {
    }

    public TransactionEntity(UUID id, UserEntity user, String cryptoSymbol, String fiatCurrency, BigDecimal amount, BigDecimal price, BigDecimal fiatRate, BigDecimal fee, TransactionType type, Instant timestamp) {
        this.id = id;
        this.user = user;
        this.cryptoSymbol = cryptoSymbol;
        this.fiatCurrency = fiatCurrency;
        this.amount = amount;
        this.price = price;
        this.fiatRate = fiatRate;
        this.fee = fee;
        this.type = type;
        this.timestamp = timestamp;
    }


}
