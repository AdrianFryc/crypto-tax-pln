package pl.cryptotax.infrastructure.database.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "pl.cryptotax.infrastructure.database.repository")
@EntityScan(basePackages = "pl.cryptotax.infrastructure.database.entity")
public class JpaConfig {
}
