package pl.cryptotax.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.cryptotax.domain.exception.InvalidCredentialsException;
import pl.cryptotax.infrastructure.database.entity.UserEntity;
import pl.cryptotax.infrastructure.database.repository.UserRepository;
import pl.cryptotax.infrastructure.rest.dto.AuthResponseDto;
import pl.cryptotax.infrastructure.rest.dto.LoginRequestDto;
import pl.cryptotax.infrastructure.security.JwtService;

@Service
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        UserEntity user  =  userRepository.findByEmail(loginRequestDto.email()).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDto(token, "Bearer");
    }
}
