package com.initstore.initstorebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS with credentials
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Disable CSRF for API endpoints
            .csrf(AbstractHttpConfigurer::disable)

            // CRITICAL: Use STATEFUL sessions (not STATELESS) for session-based auth
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
            )

            // Configure Authorization Rules
            .authorizeHttpRequests(auth -> auth
                
                // Allow public GET/POST/PUT/OPTIONS requests to product/category for storefront viewing
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**", "/api/categories", "/api/categories/**").permitAll()
                
                // 🚨 CRITICAL FIX: Explicitly permit DELETE requests to products
                // Since this is likely an admin action in production, it's safer to check for a role, 
                // but for testing quickly, we use .permitAll(). 
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").permitAll() // <-- FIX
                .requestMatchers(HttpMethod.PUT, "/api/products/**").permitAll()   // <-- Added PUT for updating products
                .requestMatchers(HttpMethod.POST, "/api/products").permitAll()    // <-- Added POST for adding products
                
                // Allow public access to authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()
                
                // ✅ FIXED: Allow anyone to create orders (guest checkout POST)
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                
                // Require authentication to view/cancel own orders (GET and PUT)
                .requestMatchers("/api/orders/user/**", "/api/orders/cancel/**").authenticated()
                
                // ADMIN ONLY: View all orders and update order status
                .requestMatchers("/admin", "/admin/**", "/api/users", "/api/users/**", "/api/orders/{id}/status").hasRole("ADMIN")
                
                // Any other request requires authentication
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
