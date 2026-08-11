package pl.cryptotax.domain.port;

import pl.cryptotax.domain.model.RawTransactionRow;

import java.io.InputStream;
import java.util.List;

public interface TransactionFileParser{
    boolean canParse(String fileName);

    List<RawTransactionRow> parse(InputStream inputStream);
}
