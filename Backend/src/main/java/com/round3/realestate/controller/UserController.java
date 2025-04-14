package com.round3.realestate.controller;

import com.round3.realestate.dtos.DashboardResponseDto;
import com.round3.realestate.entity.User;
import com.round3.realestate.services.AuthService;
import com.round3.realestate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    AuthService authService;

    @Autowired
    private UserService dashboardService;

    // Check session endpoint
    @GetMapping("/me")
    public ResponseEntity<?> checkSession() {
        try {
            User user = authService.getAuthenticatedUser();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public DashboardResponseDto getUserDashboard() {
        return dashboardService.getUserDashboard();
    }
}
