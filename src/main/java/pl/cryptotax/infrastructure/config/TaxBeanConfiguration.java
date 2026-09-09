package pl.cryptotax.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cryptotax.domain.service.NbpClient;
import pl.cryptotax.domain.service.TaxCalculationService;

@Configuration
public class TaxBeanConfiguration {
    @Bean
    public TaxCalculationService taxCalculationService(NbpClient nbpClient) {
        return new TaxCalculationService(nbpClient);
    }
}
