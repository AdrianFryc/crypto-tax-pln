package pl.cryptotax.infrastructure.csv;

import pl.cryptotax.domain.model.RawTransactionRow;
import pl.cryptotax.domain.model.TransactionType;
import pl.cryptotax.domain.port.TransactionFileParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

public class CsvTransactionParser implements TransactionFileParser {

    @Override
    public boolean canParse(String fileName) {
        if (fileName == null) {
            return false;
        }
        return fileName.toLowerCase().endsWith(".csv");
    }

    @Override
    public List<RawTransactionRow> parse(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        String[] fields = line.split(",");
                        return new RawTransactionRow(fields[0], new BigDecimal(fields[1]), new BigDecimal(fields[2]), new BigDecimal(fields[3]), TransactionType.valueOf(fields[4].trim().toUpperCase()), Instant.parse(fields[5].trim()));
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }
}
