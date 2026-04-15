package com.initstore.initstorebackend.Controller;

import com.initstore.initstorebackend.model.LoginRequest;
import com.initstore.initstorebackend.model.SignupRequest;
import com.initstore.initstorebackend.model.User;
import com.initstore.initstorebackend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // List of admin emails - add your email here
    private static final java.util.Set<String> ADMIN_EMAILS = java.util.Set.of(
        "ansh@admin.com",
        "admin@initstore.com"
        // Add more admin emails here
    );

    // ------------------------------------------------------------------
    // SIGNUP Endpoint
    // ------------------------------------------------------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already exists"));
        }

        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        // Check if email is in admin list
        newUser.setIsAdmin(ADMIN_EMAILS.contains(signupRequest.getEmail().toLowerCase()));

        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    // ------------------------------------------------------------------
    // LOGIN Endpoint - CRITICAL FIX FOR ADMIN ACCESS
    // ------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        // CRITICAL: Store user info AND admin status in session
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("isAdmin", user.getIsAdmin());
        session.setAttribute("SPRING_SECURITY_CONTEXT", 
            new org.springframework.security.core.context.SecurityContextImpl(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    user.getIsAdmin() ? 
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")) :
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                )
            )
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("user", Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "isAdmin", user.getIsAdmin()
        ));

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------------
    // LOGOUT Endpoint
    // ------------------------------------------------------------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // ------------------------------------------------------------------
    // GET Current User Info
    // ------------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not logged in"));
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        User user = userOptional.get();
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "isAdmin", user.getIsAdmin()
        ));
    }
}

// ------------------------------------------------------------------
// ADMIN USER MANAGEMENT ENDPOINTS
// Add this to a new UserManagementController.java
// ------------------------------------------------------------------