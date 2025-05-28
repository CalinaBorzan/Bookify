package com.example.bookify;

import com.example.bookify.dto.HotelDTO;
import com.example.bookify.model.Hotel;
import com.example.bookify.model.ListingType;
import com.example.bookify.model.User;
import com.example.bookify.repository.HotelRepository;
import com.example.bookify.service.HotelService;
import com.example.bookify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelServiceTest {

    @Mock
    private HotelRepository repo;

    @Mock
    private UserService userService;

    @InjectMocks
    private HotelService hotelService;

    @BeforeEach
    void initSecurityContext() {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getName()).thenReturn("tester");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private HotelDTO makeDto() {
        HotelDTO dto = new HotelDTO();
        dto.setTitle("Ocean View");
        dto.setDescription("Sea-side hotel");
        dto.setPrice(BigDecimal.valueOf(250.0));
        dto.setAddress("123 Beach Rd");
        dto.setCity("Seaville");
        dto.setStarRating(5);
        dto.setTotalRooms(100);
        dto.setCountry("Atlantis");
        dto.setAvailableFrom(LocalDate.of(2025, 9, 1));
        dto.setAvailableTo(LocalDate.of(2025, 12, 31));
        return dto;
    }

    @Test
    void create_WithValidUser_SavesHotel() {
        HotelDTO dto = makeDto();
        User creator = new User();
        creator.setUsername("tester");
        when(userService.findByUsername("tester"))
                .thenReturn(Optional.of(creator));
        when(repo.save(any(Hotel.class))).thenAnswer(i -> i.getArgument(0));

        Hotel result = hotelService.create(dto);

        ArgumentCaptor<Hotel> captor = ArgumentCaptor.forClass(Hotel.class);
        verify(repo).save(captor.capture());
        Hotel saved = captor.getValue();

        assertEquals("Ocean View", saved.getTitle());
        assertEquals("Sea-side hotel", saved.getDescription());
        // BigDecimal comparison
        assertEquals(0, BigDecimal.valueOf(250.0).compareTo(saved.getPrice()));
        assertEquals("123 Beach Rd", saved.getAddress());
        assertEquals("Seaville", saved.getCity());
        assertEquals(5, saved.getStarRating());
        assertEquals(100, saved.getTotalRooms());
        assertEquals("Atlantis", saved.getCountry());
        assertEquals(LocalDate.of(2025, 9, 1), saved.getAvailableFrom());
        assertEquals(LocalDate.of(2025, 12, 31), saved.getAvailableTo());
        assertEquals(ListingType.HOTEL, saved.getListingType());
        assertSame(creator, saved.getCreatedBy());
        assertSame(saved, result);
    }

    @Test
    void create_WhenUserNotFound_Throws() {
        when(userService.findByUsername("tester"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> hotelService.create(makeDto())
        );
        assertEquals("Auth user not found", ex.getMessage());
    }

    @Test
    void getById_Existing_ReturnsHotel() {
        Hotel h = new Hotel();
        h.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(h));

        Hotel found = hotelService.getById(7L);
        assertSame(h, found);
    }

    @Test
    void getById_NotFound_Throws() {
        when(repo.findById(8L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> hotelService.getById(8L)
        );
        assertTrue(ex.getMessage().contains("Hotel not found: 8"));
    }

    @Test
    void getAll_DelegatesToRepo() {
        List<Hotel> list = List.of(new Hotel(), new Hotel());
        when(repo.findAll()).thenReturn(list);

        List<Hotel> result = hotelService.getAll();
        assertSame(list, result);
        verify(repo).findAll();
    }

    @Test
    void update_WithExistingHotel_UpdatesAndSaves() {
        Hotel existing = new Hotel();
        existing.setId(15L);
        existing.setTitle("Old Title");
        when(repo.findById(15L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenAnswer(i -> i.getArgument(0));

        HotelDTO dto = makeDto();
        dto.setTitle("New Title");
        dto.setTotalRooms(50);

        Hotel updated = hotelService.update(15L, dto);

        assertEquals("New Title", updated.getTitle());
        assertEquals(50, updated.getTotalRooms());
        assertEquals("Atlantis", updated.getCountry());
        verify(repo).save(existing);
    }

    @Test
    void update_WhenNotFound_Throws() {
        when(repo.findById(20L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> hotelService.update(20L, makeDto())
        );
        assertTrue(ex.getMessage().contains("Hotel not found: 20"));
    }

    @Test
    void delete_WhenExists_DeletesById() {
        when(repo.existsById(3L)).thenReturn(true);

        hotelService.delete(3L);

        verify(repo).deleteById(3L);
    }

    @Test
    void delete_WhenNotExists_Throws() {
        when(repo.existsById(4L)).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> hotelService.delete(4L)
        );
        assertTrue(ex.getMessage().contains("Hotel not found: 4"));
    }
}
