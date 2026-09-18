package pl.cryptotax.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.cryptotax.domain.exception.UserAlreadyExistsException;
import pl.cryptotax.infrastructure.database.entity.UserEntity;
import pl.cryptotax.infrastructure.database.repository.UserRepository;
import pl.cryptotax.infrastructure.rest.dto.RegisterUserRequestDto;
import pl.cryptotax.infrastructure.rest.dto.UserResponseDto;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Test
    @DisplayName("Should successfully register new user when email is not taken")
    void shouldRegisterUserSuccessfully() {
        // GIVEN
        var request = new RegisterUserRequestDto("jan.kowalski@example.com", "Secret123!", "Jan", "Kowalski");
        var userId = UUID.randomUUID();
        var now = Instant.now();
        var savedEntity = new UserEntity(userId, request.email(), "hashed_Secret123!", request.firstName(), request.lastName(), now);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed_Secret123!");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // WHEN
        UserResponseDto result = userRegistrationService.registerUser(request);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo("jan.kowalski@example.com");
        assertThat(result.firstName()).isEqualTo("Jan");
        assertThat(result.lastName()).isEqualTo("Kowalski");
        assertThat(result.registerDate()).isEqualTo(now);

        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(passwordEncoder, times(1)).encode(request.password());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email is already taken")
    void shouldThrowUserAlreadyExistsExceptionWhenEmailExists() {
        // GIVEN
        var request = new RegisterUserRequestDto("jan.kowalski@example.com", "Secret123!", "Jan", "Kowalski");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> userRegistrationService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("jan.kowalski@example.com");

        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}