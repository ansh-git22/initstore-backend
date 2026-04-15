package com.initstore.initstorebackend.Controller;

import com.initstore.initstorebackend.model.User;
import com.initstore.initstorebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    // ------------------------------------------------------------------
    // GET All Users (ADMIN ONLY)
    // ------------------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            // Return users without passwords for security
            List<Map<String, Object>> userList = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    userMap.put("email", user.getEmail());
                    userMap.put("isAdmin", user.getIsAdmin());
                    return userMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ------------------------------------------------------------------
    // GET Single User by ID (ADMIN ONLY)
    // ------------------------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("isAdmin", user.getIsAdmin());
                return ResponseEntity.ok(userMap);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // ------------------------------------------------------------------
    // UPDATE User (ADMIN ONLY) - Toggle Admin Status
    // ------------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return userRepository.findById(id)
            .map(user -> {
                // Update name if provided
                if (updates.containsKey("name")) {
                    user.setName((String) updates.get("name"));
                }
                
                // Update admin status if provided
                if (updates.containsKey("isAdmin")) {
                    user.setIsAdmin((Boolean) updates.get("isAdmin"));
                }
                
                userRepository.save(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User updated successfully");
                
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("isAdmin", user.getIsAdmin());
                
                response.put("user", userMap);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
            });
    }

    // ------------------------------------------------------------------
    // DELETE User (ADMIN ONLY)
    // ------------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
            }
            
            userRepository.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Failed to delete user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
}