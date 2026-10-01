package pl.cryptotax.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.cryptotax.domain.exception.InvalidCredentialsException;
import pl.cryptotax.infrastructure.database.entity.UserEntity;
import pl.cryptotax.infrastructure.database.repository.UserRepository;
import pl.cryptotax.infrastructure.rest.dto.LoginRequestDto;
import pl.cryptotax.infrastructure.security.JwtService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_ShouldReturnAuthResponseDto_WhenCredentialsAreValid() {
        // given

        var userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        var registeredAt = Instant.parse("2023-01-01T00:00:00Z");
        var user = new UserEntity(userId, "email@example.com", "passwordHASH", "Jan", "Kowalski", registeredAt);

        when(userRepository.findByEmail("email@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("rawPassword", user.getPasswordHash()))
                .thenReturn(true);

        when(jwtService.generateToken("email@example.com"))
                .thenReturn("mocked-jwt-token");

        // when
        var response = authenticationService.login(new LoginRequestDto("email@example.com", "rawPassword"));

        // then
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mocked-jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");

    }

    @Test
    void login_ShouldThrowInvalidCredentialsException_WhenUserNotFound(){
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authenticationService.login(new LoginRequestDto("email@example.com", "rawPassword")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void login_ShouldThrowInvalidCredentialsException_WhenPasswordIsInvalid(){
        var userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        var registeredAt = Instant.parse("2023-01-01T00:00:00Z");
        var user = new UserEntity(userId, "email@example.com", "passwordHASH", "Jan", "Kowalski", registeredAt);
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("rawPassword", user.getPasswordHash()))
                .thenReturn(false);

        assertThatThrownBy(() -> authenticationService.login(new LoginRequestDto("email@example.com", "rawPassword")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }
}
