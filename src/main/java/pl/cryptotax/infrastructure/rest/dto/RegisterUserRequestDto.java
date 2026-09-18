package pl.cryptotax.infrastructure.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record RegisterUserRequestDto(@NotBlank @Email String email, @NotBlank @Size(min = 8) String password, String firstName, String lastName) {
}
