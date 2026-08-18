package pl.cryptotax.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cryptotax.domain.port.TransactionFileParser;
import pl.cryptotax.domain.port.TransactionRepository;
import pl.cryptotax.domain.service.CryptoPairParser;
import pl.cryptotax.domain.service.TransactionImportService;
import pl.cryptotax.domain.service.TransactionMapper;

import java.util.List;

@Configuration
public class DomainConfig {

    @Bean
    public CryptoPairParser cryptoPairParser() {
        return new CryptoPairParser();
    }

    @Bean
    public TransactionMapper transactionMapper(CryptoPairParser pairParser) {
        return new TransactionMapper(pairParser);
    }

    @Bean
    public TransactionImportService transactionImportService(List<TransactionFileParser> parsers,
                                                             TransactionMapper transactionMapper,
                                                             TransactionRepository transactionRepository){
        return new TransactionImportService(parsers, transactionMapper, transactionRepository);
    }
}
