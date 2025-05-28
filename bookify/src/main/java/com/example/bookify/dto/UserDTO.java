package com.example.bookify.dto;

import jakarta.validation.constraints.*;

/**
 * Data transfer object (DTO) for User information.
 * Represents data transferred between client and server for User operations.
 */
public class UserDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be ≥8 chars and include upper, lower, digit"
    )
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @Min(value = 18, message = "You must be at least 18 to register")
    private Long age;

    @NotBlank
    private String role;

    public UserDTO() {}


    /**
     * Constructs a UserDTO with specified attributes.
     *
     * @param username The username of the user.
     * @param email The email address of the user.
     * @param password The password of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param age The age of the user.
     * @param role The role of the user.
     */
    public UserDTO(String username,
                   String email,
                   String password,
                   String firstName,
                   String lastName,
                   Long age,
                   String role) {
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.age       = age;
        this.role      = role;
    }



    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getAge() {
        return age;
    }

    public String getRole() {
        return role;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
