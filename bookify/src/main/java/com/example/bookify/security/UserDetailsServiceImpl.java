package com.example.bookify.security;


import com.example.bookify.model.User;
import com.example.bookify.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * UserDetailsServiceImpl implements the UserDetailsService interface
 * to provide custom user authentication and authorization functionality.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository users;

    /**
     * Constructs a new UserDetailsServiceImpl with the UserRepository dependency.
     *
     * @param users the UserRepository used to retrieve user information
     */
    public UserDetailsServiceImpl(UserRepository users) {
        this.users = users;
    }

    /**
     * Retrieves user details by username or email.
     *
     * @param usernameOrEmail the username or email of the user to load
     * @return UserDetails object containing user details
     * @throws UsernameNotFoundException if no user with the given username or email is found
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        return (UserDetails) users.findByUsername(usernameOrEmail)
                .or(() -> users.findByEmail(usernameOrEmail))
                .orElseThrow(() ->
                        new UsernameNotFoundException("No user: " + usernameOrEmail));
    }
}
