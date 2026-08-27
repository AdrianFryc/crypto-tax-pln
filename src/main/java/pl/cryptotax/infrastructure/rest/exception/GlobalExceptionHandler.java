package pl.cryptotax.infrastructure.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

}
