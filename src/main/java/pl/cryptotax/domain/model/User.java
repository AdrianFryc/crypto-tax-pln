package pl.cryptotax.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record User(UUID id, String email, String passwordHash, String firstName, String lastName, Instant createdAt) {
}
