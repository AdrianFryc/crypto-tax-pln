package pl.cryptotax.infrastructure.nbp.dto;

import java.util.List;

public record NbpResponse(String table, String currency, String code, List<NbpRateDto> rates) {
}
