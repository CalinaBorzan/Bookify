package com.example.bookify.service;

import com.example.bookify.model.Role;
import com.example.bookify.model.User;
import com.example.bookify.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * UserService manages operations related to user management, including registration, retrieval, updating, and deletion of user records.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * Constructs a new UserService instance with required dependencies.
     *
     * @param userRepository the UserRepository for accessing user data
     * @param passwordEncoder the PasswordEncoder for encoding user passwords
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Registers a new user based on the provided user data, encoding the password before saving.
     *
     * @param dto the User entity containing user details
     * @return the registered User entity
     */
    @Transactional
    public User register(User dto) {
        User user = User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .age(dto.getAge())
                .role(Role.valueOf(String.valueOf(dto.getRole())))
                .build();
        return userRepository.save(user);
    }


    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return the User entity associated with the given ID
     * @throws RuntimeException if no user is found with the specified ID
     */
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found: " + id));
    }


    /**
     * Saves a user entity, encoding the password if it is not already encoded.
     *
     * @param user the User entity to save
     * @return the saved User entity
     */
    public User save(User user) {
        System.out.println("Saving user: " + user.getId() + ", resetToken: " + user.getResetPasswordToken());
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }


    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the User entity if found, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return an Optional containing the User entity if found, or empty if not found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    /**
     * Updates an existing user's information based on the provided user data.
     * Only non-null fields in the provided data are updated, and the password is encoded if updated.
     *
     * @param id the unique identifier of the user to update
     * @param dto the User entity containing updated user details
     * @return the updated User entity
     * @throws RuntimeException if no user is found with the specified ID
     */
    @Transactional
    public User updateUser(Long id, User dto) {
        User u = getById(id);

        if (dto.getEmail()     != null) u.setEmail(dto.getEmail());
        if (dto.getUsername()  != null) u.setUsername(dto.getUsername());
        if (dto.getPassword()  != null && !dto.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getFirstName() != null) u.setFirstName(dto.getFirstName());
        if (dto.getLastName()  != null) u.setLastName(dto.getLastName());
        if (dto.getAge()       != null) u.setAge(dto.getAge());

        return userRepository.save(u);
    }

    /**
     * Deletes a user from the system by their unique identifier, clearing associated bookings first.
     *
     * @param id the unique identifier of the user to delete
     * @throws RuntimeException if no user is found with the specified ID
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getById(id);
        System.out.println("Deleting user with ID: " + id + " and " + user.getBookings().size() + " bookings");
        user.getBookings().clear();
        userRepository.save(user);
        userRepository.deleteById(id);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all User entities
     */
    public List<User> getAll() {
        return userRepository.findAll();
    }

}