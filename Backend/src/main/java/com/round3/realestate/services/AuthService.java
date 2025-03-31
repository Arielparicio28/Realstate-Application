package com.round3.realestate.services;

import com.round3.realestate.dto.RegisterRequestDto;
import com.round3.realestate.entity.User;
import com.round3.realestate.enums.EmploymentStatus;
import com.round3.realestate.repository.UserRepository;
import com.round3.realestate.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    private HttpServletRequest request;

    public User registerUser(RegisterRequestDto registerRequestDto) {
        try {
            // Validate email uniqueness
            if(userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already taken");
            }

            // Validate username uniqueness
            if(userRepository.findByUsername(registerRequestDto.getUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
            }

            // Create and populate user entity
            User user = new User();
            user.setUsername(registerRequestDto.getUsername());
            user.setEmail(registerRequestDto.getEmail());
            user.setStatus(EmploymentStatus.UNEMPLOYED);

            // Encrypt password
            try {
                String encryptedPassword = passwordEncoder.encode(registerRequestDto.getPassword());
                user.setPassword(encryptedPassword);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing password");
            }

            // Save user
            try {
                User savedUser = userRepository.save(user);
                return savedUser;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error saving user to database: " + e.getMessage());
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Unexpected error during registration: " + e.getMessage());
        }
    }


    public String login(String emailOrUsername, String password) {
        try {
            // Intenta autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailOrUsername, password)
            );
            
            // Si llegamos aquí, la autenticación fue exitosa
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generar y retornar el token
            return jwtService.generateToken(userDetails.getId().toString(), userDetails);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    // Get authenticated user
    public User getAuthenticatedUser() {
        try {
            CustomUserDetails userDetails = getCustomUserDetails();
            return userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        } catch (Exception e) {
            System.err.println("Error en getAuthenticatedUser: " + e.getMessage());
            throw e;  // Vuelve a lanzar la excepción para que el controlador lo capture
        }
    }

    private static CustomUserDetails getCustomUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "❌ No active session");
        }

        // El principal es una instancia de CustomUserDetails
        return (CustomUserDetails) authentication.getPrincipal();
    }


    private void authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Guardar la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Guardar el contexto de seguridad en la sesión HTTP
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

}
