package com.example.bookify;

import com.example.bookify.dto.FlightDTO;
import com.example.bookify.model.Flight;
import com.example.bookify.model.ListingType;
import com.example.bookify.model.User;
import com.example.bookify.repository.FlightRepository;
import com.example.bookify.service.FlightService;
import com.example.bookify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FlightServiceTest {

    @Mock
    private FlightRepository repo;

    @Mock
    private UserService userService;

    @InjectMocks
    private FlightService flightService;

    @BeforeEach
    void initSecurityContext() {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getName()).thenReturn("pilot");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private FlightDTO makeDto() {
        FlightDTO dto = new FlightDTO();
        dto.setTitle("Transcontinental");
        dto.setDescription("Non-stop flight");
        dto.setPrice(BigDecimal.valueOf(499.99));
        dto.setAirline("SkyHigh");
        dto.setDeparture("New York");
        dto.setArrival("London");
        dto.setCountry("USA");
        dto.setDepartureTime(LocalDateTime.of(2025, 7, 1, 8, 30));
        dto.setArrivalTime(LocalDateTime.of(2025, 7, 1, 20, 15));
        dto.setSeatCapacity(180);
        return dto;
    }

    @Test
    void create_WithValidUser_SavesFlight() {
        // given
        FlightDTO dto = makeDto();
        User creator = new User();
        creator.setUsername("pilot");
        when(userService.findByUsername("pilot")).thenReturn(Optional.of(creator));
        when(repo.save(any(Flight.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Flight result = flightService.create(dto);

        // then
        ArgumentCaptor<Flight> captor = ArgumentCaptor.forClass(Flight.class);
        verify(repo).save(captor.capture());
        Flight saved = captor.getValue();

        assertEquals("Transcontinental", saved.getTitle());
        assertEquals("Non-stop flight", saved.getDescription());
        assertEquals(0, BigDecimal.valueOf(499.99).compareTo(saved.getPrice()));
        assertEquals(ListingType.FLIGHT, saved.getListingType());
        assertEquals("SkyHigh", saved.getAirline());
        assertEquals("New York", saved.getDeparture());
        assertEquals("London", saved.getArrival());
        assertEquals("USA", saved.getCountry());
        assertEquals(LocalDateTime.of(2025, 7, 1, 8, 30), saved.getDepartureTime());
        assertEquals(LocalDateTime.of(2025, 7, 1, 20, 15), saved.getArrivalTime());
        assertEquals(180, saved.getSeatCapacity());
        assertSame(creator, saved.getCreatedBy());
        assertSame(saved, result);
    }

    @Test
    void create_WhenUserNotFound_Throws() {
        when(userService.findByUsername("pilot")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> flightService.create(makeDto())
        );
        assertTrue(ex.getMessage().contains("Authenticated user not found: pilot"));
    }

    @Test
    void getById_ExistingId_ReturnsFlight() {
        Flight f = new Flight();
        f.setId(42L);
        when(repo.findById(42L)).thenReturn(Optional.of(f));

        Flight found = flightService.getById(42L);
        assertSame(f, found);
    }

    @Test
    void getById_NotFound_Throws() {
        when(repo.findById(7L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> flightService.getById(7L)
        );
        assertTrue(ex.getMessage().contains("Flight not found: 7"));
    }

    @Test
    void getAll_DelegatesToRepository() {
        List<Flight> flights = List.of(new Flight(), new Flight());
        when(repo.findAll()).thenReturn(flights);

        List<Flight> result = flightService.getAll();
        assertSame(flights, result);
        verify(repo).findAll();
    }

    @Test
    void update_WithExistingFlight_UpdatesAndSaves() {
        Flight existing = new Flight(); existing.setId(99L); existing.setTitle("Old Title");
        when(repo.findById(99L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenAnswer(i -> i.getArgument(0));

        FlightDTO dto = makeDto(); dto.setTitle("New Title"); dto.setSeatCapacity(200);

        Flight updated = flightService.update(99L, dto);

        assertEquals("New Title", updated.getTitle());
        assertEquals(200, updated.getSeatCapacity());
        verify(repo).save(existing);
    }

    @Test
    void update_WhenNotFound_Throws() {
        when(repo.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> flightService.update(123L, makeDto())
        );
        assertTrue(ex.getMessage().contains("Flight not found: 123"));
    }

    @Test
    void delete_WhenExists_Deletes() {
        when(repo.existsById(5L)).thenReturn(true);

        flightService.delete(5L);

        verify(repo).deleteById(5L);
    }

    @Test
    void delete_WhenNotExists_Throws() {
        when(repo.existsById(6L)).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> flightService.delete(6L)
        );
        assertTrue(ex.getMessage().contains("Flight not found: 6"));
    }
}