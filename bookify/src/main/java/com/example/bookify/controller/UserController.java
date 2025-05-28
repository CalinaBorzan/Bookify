package com.example.bookify.controller;

import com.example.bookify.dto.UserDTO;
import com.example.bookify.model.User;
import com.example.bookify.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Controller class for managing user-related operations.
 * Provides endpoints for retrieving, updating, and deleting users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;


    /**
     * Constructs an instance of UserController with the specified AuthService.
     *
     * @param authService The AuthService used for user authentication and operations.
     */
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @return ResponseEntity with the current user's details in a UserDTO.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User user = authService.getCurrentUser();
        UserDTO dto = new UserDTO(
                user.getUsername(),
                user.getEmail(),
                null, // Password not returned for security
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getRole().name()
        );
        return ResponseEntity.ok(dto);
    }

    /**
     * Updates the current authenticated user's information.
     *
     * @param dto The UserDTO object containing updated user details.
     * @return ResponseEntity with the updated UserDTO object.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping("/me")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO dto) {
        User updatedUser = authService.updateUser(dto);
        UserDTO responseDto = new UserDTO(
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                null,
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getAge(),
                updatedUser.getRole().name()
        );
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Deletes the current authenticated user.
     *
     * @return ResponseEntity indicating successful deletion with no content.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        authService.deleteUser();
        return ResponseEntity.noContent().build();
    }


    /**
     * Retrieves all users.
     *
     * @return ResponseEntity with a list of UserDTOs representing all users.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> dtos = authService.getAllUsers()
                .stream()
                .map(u -> new UserDTO(
                        u.getUsername(),
                        u.getEmail(),
                        null,
                        u.getFirstName(),
                        u.getLastName(),
                        u.getAge(),
                        u.getRole().name()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with the User object with the specified ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    /**
     * Updates a user's information by their ID.
     *
     * @param id  The ID of the user to update.
     * @param dto The UserDTO object containing updated user details.
     * @return ResponseEntity with the updated User object.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(authService.updateUserById(id, dto));
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity indicating successful deletion with no content.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
