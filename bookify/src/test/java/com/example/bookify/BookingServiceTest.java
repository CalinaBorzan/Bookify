package com.example.bookify;

import com.example.bookify.dto.BookingDTO;
import com.example.bookify.model.*;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.ListingRepository;
import com.example.bookify.service.BookingService;
import com.example.bookify.service.EmailService;
import com.example.bookify.service.HotelAvailabilityService;
import com.example.bookify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {

    @Mock BookingRepository repo;
    @Mock UserService userService;
    @Mock ListingRepository listingRepo;
    @Mock EmailService emailService;
    @Mock HotelAvailabilityService hotelAvailabilityService;

    @InjectMocks
    BookingService bookingService;

    @BeforeEach
    void initAuth() {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getName()).thenReturn("bob");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private User mockUser() {
        User u = new User(); u.setId(11L); u.setEmail("bob@e");
        return u;
    }

    @Test
    void create_Hotel_NoRooms_Throws() {
        BookingDTO dto = BookingDTO.builder()
                .listingId(1L)
                .checkIn(LocalDate.of(2025,6,1))
                .checkOut(LocalDate.of(2025,6,2))
                .numGuests(1)
                .build();

        Hotel h = new Hotel(); h.setId(1L); h.setTotalRooms(1); h.setListingType(ListingType.HOTEL);
        when(userService.findByUsername("bob")).thenReturn(Optional.of(mockUser()));
        when(listingRepo.findById(1L)).thenReturn(Optional.of(h));
        when(repo.countHotelOverlaps(1L, dto.getCheckIn(), dto.getCheckOut()))
                .thenReturn(1L);

        assertThrows(RuntimeException.class, () -> bookingService.create(dto));
    }

    @Test
    void create_Event_Success_SendsEmail() {
        Event e = new Event(); e.setId(2L); e.setTicketCapacity(5); e.setListingType(ListingType.EVENT);
        when(userService.findByUsername("bob")).thenReturn(Optional.of(mockUser()));
        when(listingRepo.findById(2L)).thenReturn(Optional.of(e));
        when(repo.sumEventGuests(2L)).thenReturn(1);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        BookingDTO dto = BookingDTO.builder()
                .listingId(2L)
                .checkIn(null).checkOut(null)
                .numGuests(2)
                .payNow(false)
                .build();

        Booking saved = bookingService.create(dto);
        assertEquals(BookingStatus.CONFIRMED, saved.getStatus());
        verify(emailService).sendBookingConfirmation(saved);
    }

    @Test
    void getById_NotFound_Throws() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> bookingService.getById(9L));
    }

    @Test
    void getByUser_Delegates() {
        bookingService.getByUser(3L);
        verify(repo).findByUserId(3L);
    }

    @Test
    void cancel_ChangesStatus() {
        Booking b = new Booking(); b.setId(5L); b.setStatus(BookingStatus.CONFIRMED);
        when(repo.findById(5L)).thenReturn(Optional.of(b));
        when(repo.save(b)).thenReturn(b);

        Booking cancelled = bookingService.cancel(5L);
        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void getByCurrentUser_DelegatesUsernameLookup() {
        when(userService.findByUsername("bob")).thenReturn(Optional.of(mockUser()));
        bookingService.getByCurrentUser();
        verify(repo).findByUserId(11L);
    }

    @Test
    void getDtosForCurrentUser_MapsCorrectly() {
        var user = mockUser();
        when(userService.findByUsername("bob")).thenReturn(Optional.of(user));
        var b = new Booking();
        b.setId(77L);
        b.setListing(new Hotel()); b.getListing().setId(8L); b.getListing().setTitle("X"); b.getListing().setPrice(BigDecimal.valueOf(100.0));
        b.setType(BookingType.HOTEL);
        b.setStatus(BookingStatus.CONFIRMED);
        b.setBookedAt(LocalDateTime.now());
        b.setCheckIn(LocalDate.now()); b.setCheckOut(LocalDate.now().plusDays(1));
        b.setNumGuests(2);
        when(repo.findByUserId(11L)).thenReturn(List.of(b));

        var dtos = bookingService.getDtosForCurrentUser();
        assertEquals(1, dtos.size());
        assertEquals(77L, dtos.get(0).getId());
    }

    @Test
    void cancelAsDto_ReturnsDtoWithCancelled() {
        Booking b = new Booking(); b.setId(12L); b.setUser(mockUser()); b.setListing(new Hotel());
        b.getListing().setId(4L); b.getListing().setTitle("T"); b.getListing().setPrice(BigDecimal.valueOf(50.0));
        b.setType(BookingType.HOTEL); b.setStatus(BookingStatus.CONFIRMED);
        b.setBookedAt(LocalDateTime.now());
        b.setNumGuests(1);
        when(repo.findById(12L)).thenReturn(Optional.of(b));
        when(repo.save(any())).thenReturn(b);

        var dto = bookingService.cancelAsDto(12L);
        assertEquals(BookingStatus.CANCELLED, dto.getStatus());
    }

    @Test
    void getAll_Delegates() {
        bookingService.getAll();
        verify(repo).findAll();
    }

    @Test
    void update_UpdatesFields() {
        Booking b = new Booking(); b.setId(99L); b.setStatus(BookingStatus.CONFIRMED);
        when(repo.findById(99L)).thenReturn(Optional.of(b));
        when(repo.save(b)).thenReturn(b);

        BookingDTO dto = BookingDTO.builder()
                .status(BookingStatus.CANCELLED)
                .build();
        Booking out = bookingService.update(99L, dto);
        assertEquals(BookingStatus.CANCELLED, out.getStatus());
    }

    @Test
    void delete_InvokesRepo() {
        bookingService.delete(13L);
        verify(repo).deleteById(13L);
    }
}
