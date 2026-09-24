package pl.cryptotax.infrastructure.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.cryptotax.domain.exception.InvalidCredentialsException;
import pl.cryptotax.domain.exception.InvalidTaxYearException;
import pl.cryptotax.domain.exception.UserAlreadyExistsException;
import pl.cryptotax.infrastructure.rest.dto.ErrorResponseDto;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var path = request.getRequestURI();
        var timestamp = Instant.now();
        var status = HttpStatus.BAD_REQUEST.value();
        var error = "Validation failed";
        var message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).collect(Collectors.joining(", "));
        return new ErrorResponseDto(timestamp, status, error, message, path);
    }

    @ExceptionHandler(InvalidTaxYearException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidTaxYearException(InvalidTaxYearException ex, HttpServletRequest request){
        var path = request.getRequestURI();
        var timestamp = Instant.now();
        var status = HttpStatus.BAD_REQUEST.value();
        var error = "Validation failed";
        var message = ex.getMessage();

        return new ErrorResponseDto(timestamp, status, error, message, path);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleUserAlreadyExistsException(UserAlreadyExistsException ex, HttpServletRequest request){
        var path = request.getRequestURI();
        var timestamp = Instant.now();
        var status = HttpStatus.CONFLICT.value();
        var error = "Validation failed - user already exists";
        var message = ex.getMessage();

        return new ErrorResponseDto(timestamp, status, error, message, path);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleInvalidCredentialsException(InvalidCredentialsException ex, HttpServletRequest request){
        var path = request.getRequestURI();
        var timestamp = Instant.now();
        var status = HttpStatus.UNAUTHORIZED.value();
        var error = "Validation failed - Invalid email or password";
        var message = ex.getMessage();

        return new ErrorResponseDto(timestamp, status, error, message, path);
    }
}
