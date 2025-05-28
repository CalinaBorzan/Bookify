package com.example.bookify;

import com.example.bookify.dto.EventDTO;
import com.example.bookify.model.Event;
import com.example.bookify.model.ListingType;
import com.example.bookify.model.User;
import com.example.bookify.repository.EventRepository;
import com.example.bookify.service.EventService;
import com.example.bookify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest {

    @Mock
    private EventRepository repo;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void initSecurityContext() {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getName()).thenReturn("alice");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private EventDTO makeDto() {
        EventDTO dto = new EventDTO();
        dto.setTitle("Concert");
        dto.setDescription("Live show");
        dto.setPrice(BigDecimal.valueOf(99.99));
        dto.setCountry("Wonderland");
        dto.setVenue("Main Hall");
        dto.setEventDate(LocalDate.of(2025, 12, 31).atStartOfDay());
        dto.setTicketCapacity(500);
        return dto;
    }

    @Test
    void create_WithValidUser_SavesEvent() {
        // given
        EventDTO dto = makeDto();
        User creator = new User();
        creator.setUsername("alice");
        when(userService.findByUsername("alice"))
                .thenReturn(Optional.of(creator));
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        when(repo.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Event result = eventService.create(dto);

        // then
        verify(repo).save(captor.capture());
        Event saved = captor.getValue();
        assertEquals("Concert", saved.getTitle());
        assertEquals("Live show", saved.getDescription());
        assertEquals(0, BigDecimal.valueOf(99.99).compareTo(saved.getPrice()));
        assertEquals("Wonderland", saved.getCountry());
        assertEquals("Main Hall", saved.getVenue());
        assertEquals(dto.getEventDate(), saved.getEventDate());
        assertEquals(500, saved.getTicketCapacity());
        assertEquals(ListingType.EVENT, saved.getListingType());
        assertSame(creator, saved.getCreatedBy());
        assertSame(saved, result);
    }

    @Test
    void create_WhenUserNotFound_Throws() {
        when(userService.findByUsername("alice"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> eventService.create(makeDto())
        );
        assertTrue(ex.getMessage().contains("Authenticated user not found: alice"));
    }

    @Test
    void getById_ExistingId_ReturnsEvent() {
        Event e = new Event();
        e.setId(10L);
        when(repo.findById(10L)).thenReturn(Optional.of(e));

        Event found = eventService.getById(10L);
        assertSame(e, found);
    }

    @Test
    void getById_NotFound_Throws() {
        when(repo.findById(5L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> eventService.getById(5L)
        );
        assertTrue(ex.getMessage().contains("Event not found: 5"));
    }

    @Test
    void getAll_DelegatesToRepository() {
        List<Event> list = List.of(new Event(), new Event());
        when(repo.findAll()).thenReturn(list);

        List<Event> result = eventService.getAll();
        assertSame(list, result);
        verify(repo).findAll();
    }

    @Test
    void update_WithExistingEvent_SavesUpdated() {
        Event existing = new Event();
        existing.setId(20L);
        existing.setTitle("Old");
        when(repo.findById(20L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenAnswer(i -> i.getArgument(0));

        EventDTO dto = makeDto();
        dto.setTitle("New Title");
        dto.setTicketCapacity(123);

        Event updated = eventService.update(20L, dto);

        assertEquals("New Title", updated.getTitle());
        assertEquals(123, updated.getTicketCapacity());
        verify(repo).save(existing);
    }

    @Test
    void update_WhenNotFound_Throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> eventService.update(99L, makeDto())
        );
        assertTrue(ex.getMessage().contains("Event not found: 99"));
    }

    @Test
    void delete_WhenExists_Deletes() {
        when(repo.existsById(7L)).thenReturn(true);

        eventService.delete(7L);

        verify(repo).deleteById(7L);
    }

    @Test
    void delete_WhenNotExists_Throws() {
        when(repo.existsById(8L)).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> eventService.delete(8L)
        );
        assertTrue(ex.getMessage().contains("Event not found: 8"));
    }
}
