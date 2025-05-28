package com.example.bookify;

import com.example.bookify.dto.UserDTO;
import com.example.bookify.model.Role;
import com.example.bookify.model.User;
import com.example.bookify.security.JwtUtils;
import com.example.bookify.service.AuthService;
import com.example.bookify.service.EmailService;
import com.example.bookify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock AuthenticationManager authManager;
    @Mock UserService userService;
    @Mock JwtUtils jwtUtils;
    @Mock EmailService emailService;

    @InjectMocks AuthService authService;

    private UserDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = new UserDTO();
        sampleDto.setEmail("u@e.com");
        sampleDto.setUsername("user");
        sampleDto.setPassword("pass");
        sampleDto.setFirstName("First");
        sampleDto.setLastName("Last");
        sampleDto.setAge(30L);
        sampleDto.setRole("ROLE_ADMIN");
    }

    @Test
    void login_WithValidCredentials_ReturnsJwt() {
        var creds = new UsernamePasswordAuthenticationToken("user","pass");
        Authentication auth = mock(Authentication.class);
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("user")
                .password("pass")
                .authorities(List.of())
                .build();
        lenient().when(authManager.authenticate(creds)).thenReturn(auth);
        lenient().when(auth.getPrincipal()).thenReturn(principal);
        when(jwtUtils.generateToken(principal)).thenReturn("jwt-token");

        String token = authService.login("user","pass");

        assertEquals("jwt-token", token);
    }

    @Test
    void signup_CreatesUserWithExplicitRole() {
        authService.signup(sampleDto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).register(captor.capture());
        User saved = captor.getValue();
        assertEquals("u@e.com", saved.getEmail());
        assertEquals("user", saved.getUsername());
        assertEquals(Role.ROLE_ADMIN, saved.getRole());
        assertTrue(saved.isEnabled());
    }

    @Test
    void forgotPassword_UserExists_SavesTokenAndSendsEmail() {
        User u = new User();
        u.setId(42L);
        u.setEmail("u@e.com");
        when(userService.findByEmail("u@e.com"))
                .thenReturn(Optional.of(u));

        authService.forgotPassword("u@e.com");

        verify(userService).save(argThat(inner ->
                inner.getResetPasswordToken() != null &&
                        inner.getResetTokenExpiry().isAfter(LocalDateTime.now())
        ));
        verify(emailService).sendPasswordReset(eq("u@e.com"), anyString());
    }

    @Test
    void forgotPassword_UserNotFound_NoInteractions() {
        when(userService.findByEmail("no@user")).thenReturn(Optional.empty());

        authService.forgotPassword("no@user");

        verifyNoInteractions(emailService);
    }

    @Test
    void resetPassword_ValidToken_ResetsPassword() {
        User u = new User();
        u.setEmail("u@e.com");
        u.setResetPasswordToken("token123");
        u.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        when(userService.findByEmail("u@e.com"))
                .thenReturn(Optional.of(u));

        authService.resetPassword("u@e.com", "token123", "newPass");

        verify(userService).save(argThat(inner ->
                "newPass".equals(inner.getPassword()) &&
                        inner.getResetPasswordToken() == null &&
                        inner.getResetTokenExpiry() == null
        ));
    }

    @Test
    void resetPassword_InvalidOrExpired_ThrowsException() {
        User u = new User();
        u.setEmail("u@e.com");
        u.setResetPasswordToken("token123");
        u.setResetTokenExpiry(LocalDateTime.now().minusMinutes(5));
        when(userService.findByEmail("u@e.com"))
                .thenReturn(Optional.of(u));

        assertThrows(RuntimeException.class, () ->
                authService.resetPassword("u@e.com", "token123", "pw"));
    }

    @Test
    void getCurrentUser_NoAuthentication_Throws() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertThrows(RuntimeException.class,
                () -> authService.getCurrentUser());
    }

    @Test
    void getCurrentUser_AuthenticatedUser_ReturnsFromService() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("user");
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUsername("user");
        when(userService.findByUsername("user"))
                .thenReturn(Optional.of(user));

        User result = authService.getCurrentUser();
        assertSame(user, result);
    }

    void updateUser_WithPartialDto_InvokesUpdate() {
        // spy the service
        AuthService spySvc = spy(authService);
        // stub out getCurrentUser without invoking it
        User existing = new User();
        existing.setId(5L);
        doReturn(existing).when(spySvc).getCurrentUser();

        UserDTO dto = new UserDTO();
        dto.setEmail("new@e.com");

        User updated = new User();
        updated.setEmail("new@e.com");
        when(userService.updateUser(eq(5L), any(User.class))).thenReturn(updated);

        User result = spySvc.updateUser(dto);
        assertEquals("new@e.com", result.getEmail());
    }

    @Test
    void deleteUser_InvokesServiceDelete() {
        AuthService spySvc = spy(authService);
        User existing = new User();
        existing.setId(7L);
        // again use doReturn to stub getCurrentUser
        doReturn(existing).when(spySvc).getCurrentUser();

        spySvc.deleteUser();
        verify(userService).deleteUser(7L);
    }

    @Test
    void getAllUsers_DelegatesToUserService() {
        authService.getAllUsers();
        verify(userService).getAll();
    }

    @Test
    void getUserById_DelegatesToUserService() {
        authService.getUserById(99L);
        verify(userService).getById(99L);
    }

    @Test
    void updateUserById_DelegatesWithCorrectArgs() {
        User target = new User();
        target.setId(4L);
        AuthService spySvc = spy(authService);
        doReturn(target).when(spySvc).getUserById(4L);

        UserDTO dto = new UserDTO();
        dto.setFirstName("Changed");
        spySvc.updateUserById(4L, dto);

        verify(userService).updateUser(eq(4L), any(User.class));
    }

    @Test
    void deleteUserById_DelegatesToUserService() {
        authService.deleteUserById(123L);
        verify(userService).deleteUser(123L);
    }
}
