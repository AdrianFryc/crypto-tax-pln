package pl.cryptotax.domain.model;

import java.util.Arrays;

public enum FiatCurrency {
    PLN,
    USD,
    EUR;

    public static boolean isFiat(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            return false;
        }
        return Arrays.stream(values())
                .anyMatch(fiat -> fiat.name().equalsIgnoreCase(symbol.trim()));
    }
}

