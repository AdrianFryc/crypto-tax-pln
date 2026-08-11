package pl.cryptotax.domain.service;

import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.port.TransactionFileParser;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class TransactionImportService {

    private final List<TransactionFileParser> parsers;
    private final TransactionMapper transactionMapper;

    public TransactionImportService(List<TransactionFileParser> parsers, TransactionMapper transactionMapper) {
        this.parsers = parsers;
        this.transactionMapper = transactionMapper;
    }

    public List<CryptoTransaction> importTransactions(String fileName, InputStream inputStream){

        var fileParser = parsers.stream()
                .filter(parser -> parser.canParse(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file format: " + fileName));

        var parsedTransactions = fileParser.parse(inputStream);

        return parsedTransactions.stream()
                .map(transactionMapper::map)
                .flatMap(Optional::stream)
                .toList();
    }
}