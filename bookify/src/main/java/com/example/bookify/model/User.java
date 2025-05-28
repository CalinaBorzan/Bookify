package com.example.bookify.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity class representing a user in the system.
 */
@Entity
@Table(name="users")
public class User implements UserDetails {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Long age;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String resetPasswordToken;
    private LocalDateTime resetTokenExpiry;

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Booking> bookings = new ArrayList<>();

    // --- Private ctor used by Builder ---
    private User(Builder b) {
        this.email                = b.email;
        this.username             = b.username;
        this.password             = b.password;
        this.firstName            = b.firstName;
        this.lastName             = b.lastName;
        this.age                  = b.age;
        this.role                 = b.role;
        this.resetPasswordToken   = b.resetPasswordToken;
        this.resetTokenExpiry     = b.resetTokenExpiry;
        this.bookings             = new ArrayList<>(b.bookings);
    }

    public User() {

    }

    /**
     * Builder pattern for constructing User instances.
     */
    public static class Builder {
        private String email;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private Long age;
        private Role role;
        private boolean enabled = true;
        private String resetPasswordToken;
        private LocalDateTime resetTokenExpiry;
        private List<Booking> bookings = new ArrayList<>();

        public Builder email(String email) {
            this.email = email; return this;
        }
        public Builder username(String u) {
            this.username = u; return this;
        }
        public Builder password(String p) {
            this.password = p; return this;
        }
        public Builder firstName(String fn) {
            this.firstName = fn; return this;
        }
        public Builder lastName(String ln) {
            this.lastName = ln; return this;
        }
        public Builder age(Long a) {
            this.age = a; return this;
        }
        public Builder role(Role r) {
            this.role = r; return this;
        }
        public Builder enabled(boolean e) {
            this.enabled = e; return this;
        }
        public Builder resetPasswordToken(String t) {
            this.resetPasswordToken = t; return this;
        }
        public Builder resetTokenExpiry(LocalDateTime dt) {
            this.resetTokenExpiry = dt; return this;
        }
        public Builder bookings(List<Booking> bks) {
            this.bookings = new ArrayList<>(bks); return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // --- Manual getters & setters below ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @Override public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    @Override public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Long getAge() { return age; }
    public void setAge(Long age) { this.age = age; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getResetPasswordToken() { return resetPasswordToken; }
    public void setResetPasswordToken(String t) { this.resetPasswordToken = t; }
    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime dt) { this.resetTokenExpiry = dt; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bs) { this.bookings = bs; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role.name());
    }
    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
}
