// src/main/java/com/example/bookify/security/WebSecurityConfig.java
package com.example.bookify.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * WebSecurityConfig configures security settings for the application.
 * It defines authentication, authorization rules, and JWT token handling.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final JwtAuthenticationEntryPoint entryPoint;

    /**
     * Constructs a new WebSecurityConfig instance with required dependencies.
     *
     * @param uds the UserDetailsServiceImpl instance for user details management
     * @param jwtUtils the JwtUtils instance for JWT token operations
     * @param entryPoint the JwtAuthenticationEntryPoint instance for handling authentication errors
     */
    public WebSecurityConfig(UserDetailsServiceImpl uds,
                             JwtUtils jwtUtils,
                             JwtAuthenticationEntryPoint entryPoint) {
        this.userDetailsService = uds;
        this.jwtUtils = jwtUtils;
        this.entryPoint = entryPoint;
    }

    /**
     * Configures the AuthenticationManager bean using the provided AuthenticationConfiguration.
     *
     * @param cfg the AuthenticationConfiguration to configure AuthenticationManager
     * @return AuthenticationManager bean instance
     * @throws Exception if an error occurs while configuring AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    /**
     * Configures the SecurityFilterChain for HTTP security configurations.
     *
     * @param http the HttpSecurity to configure security filters
     * @return SecurityFilterChain instance for managing HTTP security
     * @throws Exception if an error occurs while configuring HttpSecurity
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthTokenFilter jwtFilter = new JwtAuthTokenFilter(jwtUtils, userDetailsService);

        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,   "/api/bookings").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/bookings/me").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/bookings/*/cancel").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/bookings/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/listings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/listings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/listings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/listings/**").hasAnyRole("USER","ADMIN")

                        .requestMatchers("/api/bookings/**").hasRole("USER")

                        .requestMatchers("/api/payments/**", "/api/reviews/**")
                        .hasAnyRole("USER","ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
