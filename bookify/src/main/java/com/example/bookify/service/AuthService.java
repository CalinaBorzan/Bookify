package com.example.bookify.service;

import com.example.bookify.dto.UserDTO;
import com.example.bookify.model.Role;
import com.example.bookify.model.User;
import com.example.bookify.security.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AuthService provides authentication and user-related services.
 * It handles user login, signup, password management, and JWT token generation.
 */
@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    /**
     * Constructs a new AuthService instance with required dependencies.
     *
     * @param authManager the AuthenticationManager instance for user authentication
     * @param userService the UserService instance for user management
     * @param jwtUtils the JwtUtils instance for JWT token operations
     * @param emailService the EmailService instance for sending email notifications
     */
    public AuthService(AuthenticationManager authManager,
                       UserService userService,
                       JwtUtils jwtUtils,
                       EmailService emailService) {
        this.authManager = authManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    /**
     * Authenticates the user and generates a JWT token upon successful login.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return a JWT token string
     */
    public String login(String username, String password) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return jwtUtils.generateToken(
                (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()
        );
    }

    /**
     * Registers a new user based on the provided DTO.
     *
     * @param dto the UserDTO containing user details
     */
    @Transactional
    public void signup(UserDTO dto) {
        User user = User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .age(dto.getAge())
                .role(dto.getRole() != null ? Role.valueOf(dto.getRole()) : Role.ROLE_USER)
                .enabled(true)
                .build();
        userService.register(user);
    }

    /**
     * Initiates the password reset process for the given email address.
     * Generates a reset token, saves it to the user entity, and sends an email with instructions.
     *
     * @param email the email address of the user requesting password reset
     */
    @Transactional
    public void forgotPassword(String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            System.out.println("Saving reset token for user: " + user.getId());
            userService.save(user);
            emailService.sendPasswordReset(email, token);
        }
    }

    /**
     * Resets the password for the user identified by the email address and token.
     *
     * @param email the email address of the user
     * @param token the reset token sent to the user
     * @param newPassword the new password to set
     */
    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user with email " + email));
        if (!token.equals(user.getResetPasswordToken()) ||
                user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired reset token");
        }
        user.setPassword(newPassword);
        user.setResetPasswordToken(null);
        user.setResetTokenExpiry(null);
        System.out.println("Saving new password for user: " + user.getId());
        userService.save(user);
    }

    /**
     * Retrieves the currently authenticated user's username from the security context.
     *
     * @return the username of the authenticated user
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        String username = auth.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Updates the currently authenticated user's information based on the provided DTO.
     * Only fields provided in the DTO are updated, while unspecified fields retain their existing values.
     * The user's role and enabled status remain unchanged.
     *
     * @param dto the UserDTO containing updated user details
     * @return the updated User entity
     * @throws RuntimeException if no authenticated user is found or if the user to update does not exist
     */
    @Transactional
    public User updateUser(UserDTO dto) {
        User current = getCurrentUser();
        User toSave = User.builder()
                .email(dto.getEmail() != null ? dto.getEmail() : current.getEmail())
                .username(dto.getUsername() != null ? dto.getUsername() : current.getUsername())
                .password(dto.getPassword() != null && !dto.getPassword().isBlank() ? dto.getPassword() : null)
                .firstName(dto.getFirstName() != null ? dto.getFirstName() : current.getFirstName())
                .lastName(dto.getLastName() != null ? dto.getLastName() : current.getLastName())
                .age(dto.getAge() != null ? dto.getAge() : current.getAge())
                .role(current.getRole())
                .enabled(current.isEnabled())
                .build();
        return userService.updateUser(current.getId(), toSave);
    }

    /**
     * Deletes the currently authenticated user from the system.
     *
     * @throws RuntimeException if no authenticated user is found or if the user to delete does not exist
     */
    @Transactional
    public void deleteUser() {
        User currentUser = getCurrentUser();
        userService.deleteUser(currentUser.getId());
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of all User entities
     */
    public List<User> getAllUsers() {
        return userService.getAll();
    }


    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return the User entity associated with the given ID
     * @throws RuntimeException if no user is found with the specified ID
     */
    public User getUserById(Long id) {
        return userService.getById(id);
    }

    /**
     * Updates a user's information based on the provided DTO and user ID.
     * Only fields provided in the DTO are updated, while unspecified fields retain their existing values.
     * The user's role remains unchanged.
     *
     * @param id the unique identifier of the user to update
     * @param dto the UserDTO containing updated user details
     * @return the updated User entity
     * @throws RuntimeException if no user is found with the specified ID
     */
    @Transactional
    public User updateUserById(Long id, UserDTO dto) {
        User user = getUserById(id);
        User toSave = User.builder()
                .email(dto.getEmail() != null ? dto.getEmail() : user.getEmail())
                .username(dto.getUsername() != null ? dto.getUsername() : user.getUsername())
                .password(dto.getPassword() != null && !dto.getPassword().isBlank() ? dto.getPassword() : null)
                .firstName(dto.getFirstName() != null ? dto.getFirstName() : user.getFirstName())
                .lastName(dto.getLastName() != null ? dto.getLastName() : user.getLastName())
                .age(dto.getAge() != null ? dto.getAge() : user.getAge())
                .role(user.getRole())
                .build();
        return userService.updateUser(id, toSave);
    }

    /**
     * Deletes a user from the system by their unique identifier.
     *
     * @param id the unique identifier of the user to delete
     * @throws RuntimeException if no user is found with the specified ID
     */
    @Transactional
    public void deleteUserById(Long id) {
        userService.deleteUser(id);
    }
}