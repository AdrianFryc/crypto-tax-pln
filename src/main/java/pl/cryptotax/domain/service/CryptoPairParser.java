package pl.cryptotax.domain.service;

import pl.cryptotax.domain.model.FiatCurrency;
import pl.cryptotax.domain.model.ParsedPair;

import java.util.Arrays;
import java.util.Optional;

public class CryptoPairParser{
    public Optional<ParsedPair> parse(String rawPair){
        if(rawPair == null || rawPair.isBlank()){
            return Optional.empty();
        }
        var clearedPair = rawPair.trim().toUpperCase().replace("/", "").replace("-", "").replace("_", "");

        return Arrays.stream(FiatCurrency.values())
                .filter(fiat -> clearedPair.endsWith(fiat.name()))
                .findFirst()
                .flatMap(fiat -> {
                    var cryptoSymbol = clearedPair.substring(0, clearedPair.length() - fiat.name().length());
                    if(cryptoSymbol.isBlank() || FiatCurrency.isFiat(cryptoSymbol))
                        return Optional.empty();
                    else{
                       return Optional.of(new ParsedPair(cryptoSymbol, fiat.name()));
                    }

                });
    }
}
