package pl.cryptotax.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.cryptotax.infrastructure.database.entity.UserEntity;
import pl.cryptotax.infrastructure.database.repository.UserRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userRepository);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldAuthenticateUser_WhenTokenIsValid() throws ServletException, IOException {
        // given
        String token = "valid-token";
        String email = "email@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        UserEntity user = new UserEntity(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                email,
                "passwordHASH",
                "Jan",
                "Kowalski",
                Instant.parse("2023-01-01T00:00:00Z")
        );

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token)).thenReturn(true);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(user);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenHeaderIsMissing() throws ServletException, IOException {
        // given - brak nagłówka w request

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenHeaderPrefixIsInvalid() throws ServletException, IOException {
        // given
        request.addHeader("Authorization", "Basic c29tZV9jcmVkZW50aWFscw==");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenTokenIsInvalid() throws ServletException, IOException {
        // given
        String token = "invalid-token";
        String email = "user@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        UserEntity user = new UserEntity(
                UUID.randomUUID(), email, "hash", "Jan", "Kowalski", Instant.now()
        );

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token)).thenReturn(false); // Token wygasł/jest niepoprawny

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenUserDoesNotExist() throws ServletException, IOException {
        // given
        String token = "mock-token";
        String email = "notfound@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty()); // Brak użytkownika w DB

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(request, response);
    }
}