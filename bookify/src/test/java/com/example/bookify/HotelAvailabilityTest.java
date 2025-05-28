package com.example.bookify;

import com.example.bookify.model.Hotel;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.HotelRepository;
import com.example.bookify.service.HotelAvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelAvailabilityServiceTest {

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private HotelRepository hotelRepo;

    @InjectMocks
    private HotelAvailabilityService availabilityService;

    private final LocalDate checkIn  = LocalDate.of(2025, 8, 1);
    private final LocalDate checkOut = LocalDate.of(2025, 8, 5);

    @Test
    void isAvailable_WhenHotelNotFound_ThrowsException() {
        when(hotelRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> availabilityService.isAvailable(99L, checkIn, checkOut)
        );
        assertTrue(ex.getMessage().contains("Hotel not found 99"));
    }

    @Test
    void isAvailable_ReturnsTrue_WhenOccupiedLessThanTotalRooms() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setTotalRooms(10);
        when(hotelRepo.findById(1L)).thenReturn(Optional.of(hotel));
        when(bookingRepo.countHotelOverlaps(1L, checkIn, checkOut)).thenReturn(5L);

        boolean available = availabilityService.isAvailable(1L, checkIn, checkOut);

        assertTrue(available);
        verify(bookingRepo).countHotelOverlaps(1L, checkIn, checkOut);
    }

    @Test
    void isAvailable_ReturnsFalse_WhenOccupiedEqualsTotalRooms() {
        Hotel hotel = new Hotel();
        hotel.setId(2L);
        hotel.setTotalRooms(5);
        when(hotelRepo.findById(2L)).thenReturn(Optional.of(hotel));
        when(bookingRepo.countHotelOverlaps(2L, checkIn, checkOut)).thenReturn(5L);

        boolean available = availabilityService.isAvailable(2L, checkIn, checkOut);

        assertFalse(available);
        verify(bookingRepo).countHotelOverlaps(2L, checkIn, checkOut);
    }

    @Test
    void isAvailable_ReturnsFalse_WhenOccupiedGreaterThanTotalRooms() {
        Hotel hotel = new Hotel();
        hotel.setId(3L);
        hotel.setTotalRooms(3);
        when(hotelRepo.findById(3L)).thenReturn(Optional.of(hotel));
        when(bookingRepo.countHotelOverlaps(3L, checkIn, checkOut)).thenReturn(4L);

        boolean available = availabilityService.isAvailable(3L, checkIn, checkOut);

        assertFalse(available);
        verify(bookingRepo).countHotelOverlaps(3L, checkIn, checkOut);
    }
}
