package pl.cryptotax.infrastructure.rest.mapper;

import org.springframework.stereotype.Component;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.infrastructure.rest.dto.CreateTransactionRequestDto;
import pl.cryptotax.infrastructure.rest.dto.TransactionResponseDto;

import java.util.UUID;

@Component
public class TransactionRestMapper {
    public CryptoTransaction toDomain(CreateTransactionRequestDto dto){
        return new CryptoTransaction(
                UUID.randomUUID(),
                dto.cryptoSymbol(),
                dto.cryptoAmount(),
                dto.price(),
                dto.cryptoAmount().multiply(dto.price()),
                dto.fee(),
                dto.fiatCurrency(),
                dto.transactionType(),
                dto.transactionDate()
        );
    }

    public TransactionResponseDto toDto(CryptoTransaction domain){
        return new TransactionResponseDto(
                domain.transactionId(),
                domain.cryptoSymbol(),
                domain.cryptoAmount(),
                domain.fiatRate(),
                domain.fee(),
                domain.fiatAmount(),
                domain.fiatCurrency(),
                domain.transactionType(),
                domain.transactionDate()
        );
    }
}
