package pl.cryptotax.infrastructure.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cryptotax.application.service.AuthenticationService;
import pl.cryptotax.application.service.UserRegistrationService;
import pl.cryptotax.infrastructure.rest.dto.AuthResponseDto;
import pl.cryptotax.infrastructure.rest.dto.LoginRequestDto;
import pl.cryptotax.infrastructure.rest.dto.RegisterUserRequestDto;
import pl.cryptotax.infrastructure.rest.dto.UserResponseDto;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserRegistrationService userRegistrationService;

    public AuthController(AuthenticationService authenticationService, UserRegistrationService userRegistrationService) {
        this.authenticationService = authenticationService;
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authenticationService.login(loginRequestDto));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerUser(@Valid @RequestBody RegisterUserRequestDto request) {
        return userRegistrationService.registerUser(request);
    }
}
