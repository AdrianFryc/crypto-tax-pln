package pl.cryptotax.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.cryptotax.infrastructure.database.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // 1. Guard Clause: Jeśli brak tokena lub złe słowo kluczowe -> puść żądanie dalej i wyjdź
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Wyciągnięcie czystego tokena
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractEmail(jwt);

        // 3. Sprawdzenie, czy mamy email ORAZ czy użytkownik nie jest jeszcze zalogowany w kontekście
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userRepository.findByEmail(userEmail).ifPresent(user -> {
                if (jwtService.isTokenValid(jwt)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of() // pusta lista uprawnień (brak ról)
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            });
        }

        // 4. ZAWSZE na samym końcu przepuszczamy żądanie do kolejnych filtrów
        filterChain.doFilter(request, response);
    }
}
