package com.initstore.initstorebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    // ✅ FIXED: Specify exact origins instead of wildcard when using credentials
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 🔧 CRITICAL FIX: Specify exact origins (cannot use "*" with credentials)
        // Add your production frontend URL here when deploying
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",           // React development
            "http://localhost:3001",           // Backup port
            "https://initstore-frontend-new.onrender.com",  // Your production frontend
            "https://your-custom-domain.com"   // Add your custom domain if you have one
        ));
        
        // Allow all standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // ✅ Allow sending cookies/session - REQUIRED for session-based auth
        configuration.setAllowCredentials(true);
        
        // Expose headers if needed
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie"));
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Disable CSRF for REST APIs (you may want to enable this in production)
            .csrf(AbstractHttpConfigurer::disable)

            // Session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**", "/api/categories", "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                
                // Authenticated user endpoints
                .requestMatchers("/api/orders/user/**", "/api/orders/cancel/**").authenticated()
                
                // Admin-only endpoints
                .requestMatchers("/admin", "/admin/**", "/api/users", "/api/users/**", "/api/orders/{id}/status").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}