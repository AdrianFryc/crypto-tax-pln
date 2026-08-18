package pl.cryptotax.domain.service;

import org.springframework.transaction.annotation.Transactional;
import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.port.TransactionFileParser;
import pl.cryptotax.domain.port.TransactionRepository;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class TransactionImportService {

    private final List<TransactionFileParser> parsers;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    public TransactionImportService(List<TransactionFileParser> parsers, TransactionMapper transactionMapper, TransactionRepository transactionRepository) {
        this.parsers = parsers;
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public List<CryptoTransaction> importTransactions(String fileName, InputStream inputStream){

        var fileParser = parsers.stream()
                .filter(parser -> parser.canParse(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file format: " + fileName));

        var parsedTransactions = fileParser.parse(inputStream);

        var mappedTransactions = parsedTransactions.stream()
                .map(transactionMapper::map)
                .flatMap(Optional::stream)
                .toList();

        return transactionRepository.saveAll(mappedTransactions);
    }
}