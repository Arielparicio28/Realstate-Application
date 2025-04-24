package com.round3.realestate.security;

import com.round3.realestate.entity.User;
import com.round3.realestate.repository.RevokedTokenRepository;
import com.round3.realestate.repository.UserRepository;
import com.round3.realestate.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    final HandlerExceptionResolver handlerExceptionResolver;
    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository; // Inyectamos el repositorio para buscar por ID

    public JwtAuthenticationFilter(
            JwtService jwtService,
            HandlerExceptionResolver handlerExceptionResolver,
            RevokedTokenRepository revokedTokenRepository,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.revokedTokenRepository = revokedTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");

            // Si no hay token, continuamos con la cadena de filtros
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);
            
            // Primero verificamos si el token está revocado
            if (isTokenRevoked(jwt)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has been revoked");
            }

            // Extraemos el ID del usuario y validamos
            final String userIdStr = jwtService.extractUserId(jwt);
            if (userIdStr == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }

            // Convertimos el ID a Long y validamos
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user ID in token");
            }

            // Si no hay autenticación en el contexto, la establecemos
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Buscamos el usuario
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

                // Creamos los detalles del usuario
                CustomUserDetails userDetails = new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword()
                );

                // Validamos el token
                if (!jwtService.isTokenValid(jwt, userDetails)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
                }

                // Creamos y establecemos la autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (ResponseStatusException e) {
            response.setStatus(e.getStatusCode().value());
            response.getWriter().write(String.format("{\"status\":%d,\"message\":\"%s\"}", 
                e.getStatusCode().value(), e.getReason()));
            response.setContentType("application/json");
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"status\":401,\"message\":\"Authentication failed\"}");
            response.setContentType("application/json");
        }
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.findByToken(token).isPresent();
    }
}
