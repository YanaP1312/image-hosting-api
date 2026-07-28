package com.image.hosting.config.auth;

import com.image.hosting.repositories.SessionRepository;
import com.image.hosting.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class OpaqueTokenAuthenticationFilter extends OncePerRequestFilter {

    private final SessionRepository sessionRepository;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String rawToken = header.substring(7);
            String hashedToken = tokenService.hashToken(rawToken);

            sessionRepository.findSessionById(hashedToken)
                    .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                    .ifPresent(session -> {
                        var auth = new UsernamePasswordAuthenticationToken(
                                session.getUserId(), null, List.of()
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
        }

        filterChain.doFilter(request, response);
    }
}
