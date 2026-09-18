package pl.cryptotax.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cryptotax.infrastructure.database.entity.UserEntity;
import pl.cryptotax.infrastructure.database.repository.UserRepository;
import pl.cryptotax.infrastructure.rest.dto.RegisterUserRequestDto;
import pl.cryptotax.infrastructure.rest.dto.UserResponseDto;
import pl.cryptotax.domain.exception.UserAlreadyExistsException;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto registerUser(RegisterUserRequestDto request) {
        if(userRepository.existsByEmail(request.email())){
            throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
        }
        var userId = UUID.randomUUID();
        var registeredAt = Instant.now();
        var user = new UserEntity(userId, request.email(), passwordEncoder.encode(request.password()), request.firstName(), request.lastName(), registeredAt);
        var savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getCreatedAt());
    }
}
