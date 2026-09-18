package pl.cryptotax.infrastructure.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.cryptotax.application.service.UserRegistrationService;
import pl.cryptotax.infrastructure.rest.dto.RegisterUserRequestDto;
import pl.cryptotax.infrastructure.rest.dto.UserResponseDto;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserRegistrationService userRegistrationService;

    public UserController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerUser(@Valid @RequestBody RegisterUserRequestDto request) {
        return userRegistrationService.registerUser(request);
    }
}
