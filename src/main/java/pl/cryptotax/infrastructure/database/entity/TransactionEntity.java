package pl.cryptotax.infrastructure.database.entity;

import jakarta.persistence.*;
import pl.cryptotax.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String symbol;

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

    public TransactionEntity(String symbol, BigDecimal amount, BigDecimal price, BigDecimal fiatRate, BigDecimal fee, TransactionType type, Instant timestamp) {
        this.symbol = symbol;
        this.amount = amount;
        this.price = price;
        this.fiatRate = fiatRate;
        this.fee = fee;
        this.type = type;
        this.timestamp = timestamp;
    }


    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFiatRate() {
        return fiatRate;
    }

    public void setFiatRate(BigDecimal fiatRate) {
        this.fiatRate = fiatRate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


}
