package com.example.bookify.controller;

import com.example.bookify.dto.UserDTO;
import com.example.bookify.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
/**
 * Controller class for handling authentication-related operations.
 * This class manages user login, signup, password reset, and related operations.
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final AuthService auth;

    /**
     * Constructs an instance of AuthController with the specified AuthService.
     *
     * @param auth The AuthService implementation used for authentication operations.
     */
    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    /**
     * Represents a login request object with username and password.
     */
    public static record LoginRequest(String username, String password) {}

    /**
     * Represents a reset password request object with email, token, and new password.
     */
    public static record ResetRequest(String email, String token, String newPassword) {}

    /**
     * Handles HTTP POST requests to /api/auth/login for user authentication.
     *
     * @param req The LoginRequest object containing username and password.
     * @return ResponseEntity containing a JWT token upon successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        String jwt = auth.login(req.username(), req.password());
        return ResponseEntity.ok(jwt);
    }

    /**
     * Handles HTTP POST requests to /api/auth/signup for user registration.
     *
     * @param dto The UserDTO object containing user details for registration.
     * @return ResponseEntity with HTTP status 201 CREATED upon successful signup.
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserDTO dto) {
        auth.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Handles HTTP POST requests to /api/auth/forgot-password for resetting user password.
     *
     * @param email The email address of the user requesting password reset.
     * @return ResponseEntity indicating successful initiation of password reset process.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        auth.forgotPassword(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Handles HTTP POST requests to /api/auth/reset-password for resetting user password with token verification.
     *
     * @param req The ResetRequest object containing email, token, and new password.
     * @return ResponseEntity indicating successful password reset or error message in case of failure.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetRequest req) {
        try {
            auth.resetPassword(req.email(), req.token(), req.newPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
