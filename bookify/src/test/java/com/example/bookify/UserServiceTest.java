package com.example.bookify;

import com.example.bookify.model.User;
import com.example.bookify.model.Role;
import com.example.bookify.repository.UserRepository;
import com.example.bookify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User dto;

    @BeforeEach
    void setUp() {
        dto = new User();
        dto.setEmail("x@x.com");
        dto.setUsername("usr");
        dto.setPassword("rawpwd");
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setAge(28L);
        dto.setRole(Role.ROLE_USER);
    }

    @Test
    void register_EncodesPasswordAndSaves() {
        when(passwordEncoder.encode("rawpwd")).thenReturn("encpwd");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.register(dto);

        verify(passwordEncoder).encode("rawpwd");
        verify(userRepository).save(captor.capture());
        User arg = captor.getValue();
        assertEquals("encpwd", arg.getPassword());
        assertEquals("x@x.com", arg.getEmail());
        assertEquals("usr", arg.getUsername());
    }

    @Test
    void getById_Existing_ReturnsUser() {
        User u = new User();
        u.setId(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(u));

        assertSame(u, userService.getById(3L));
    }

    @Test
    void getById_NotFound_Throws() {
        when(userRepository.findById(4L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.getById(4L)
        );
        assertTrue(ex.getMessage().contains("User not found: 4"));
    }

    @Test
    void save_WhenRawPassword_EncodesAndSaves() {
        User u = new User();
        u.setPassword("plain");
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(u)).thenReturn(u);

        User out = userService.save(u);
        assertEquals("encoded", out.getPassword());
        verify(userRepository).save(u);
    }

    @Test
    void save_WhenAlreadyEncoded_SkipsEncoding() {
        User u = new User();
        u.setPassword("$2a_hash");
        when(userRepository.save(u)).thenReturn(u);

        User out = userService.save(u);
        assertEquals("$2a_hash", out.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void findByUsername_Delegates() {
        userService.findByUsername("usr");
        verify(userRepository).findByUsername("usr");
    }

    @Test
    void findByEmail_Delegates() {
        userService.findByEmail("e@e");
        verify(userRepository).findByEmail("e@e");
    }

    @Test
    void updateUser_WithChanges_EncodesPasswordAndSaves() {
        User existing = new User();
        existing.setId(5L);
        existing.setPassword("$2a_old");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpwd")).thenReturn("encnew");
        when(userRepository.save(existing)).thenReturn(existing);

        User dto = new User();
        dto.setPassword("newpwd");
        dto.setEmail("new@e");
        dto.setFirstName("Nancy");

        User out = userService.updateUser(5L, dto);

        assertEquals("new@e", out.getEmail());
        assertEquals("Nancy", out.getFirstName());
        assertEquals("encnew", out.getPassword());
    }

    @Test
    void deleteUser_RemovesBookingsAndDeletes() {
        User u = new User();
        u.setId(6L);
        u.getBookings().add(null);
        when(userRepository.findById(6L)).thenReturn(Optional.of(u));

        userService.deleteUser(6L);

        assertTrue(u.getBookings().isEmpty());
        verify(userRepository).save(u);
        verify(userRepository).deleteById(6L);
    }

    @Test
    void getAll_Delegates() {
        userService.getAll();
        verify(userRepository).findAll();
    }
}
