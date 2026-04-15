package com.initstore.initstorebackend.model;

import jakarta.persistence.*;

/**
 * Represents a User entity for accounts and authentication.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Using is_admin column from your database
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false; // Default is false

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
    
    // Helper method to get role as String for Spring Security
    public String getRole() {
        return isAdmin ? "ADMIN" : "USER";
    }
}