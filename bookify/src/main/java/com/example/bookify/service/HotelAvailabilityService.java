package com.example.bookify.service;

import com.example.bookify.model.Hotel;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * HotelAvailabilityService manages operations related to checking hotel room availability.
 */
@Service
public class HotelAvailabilityService {
    private final BookingRepository bookingRepo;
    private final HotelRepository hotelRepo;

    /**
     * Constructs a new HotelAvailabilityService instance with required dependencies.
     *
     * @param bookingRepo the BookingRepository for accessing booking data
     * @param hotelRepo the HotelRepository for accessing hotel data
     */
    public HotelAvailabilityService(
            BookingRepository bookingRepo,
            HotelRepository hotelRepo
    ) {
        this.bookingRepo = bookingRepo;
        this.hotelRepo   = hotelRepo;
    }


    /**
     * Checks if a hotel has available rooms for the specified date range.
     *
     * @param hotelId the unique identifier of the hotel
     * @param checkIn the check-in date for the booking
     * @param checkOut the check-out date for the booking
     * @return true if rooms are available, false otherwise
     * @throws RuntimeException if no hotel is found with the specified ID
     */
    public boolean isAvailable(Long hotelId,
                               LocalDate checkIn,
                               LocalDate checkOut) {
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found "+hotelId));

        long occupied = bookingRepo.countHotelOverlaps(hotelId, checkIn, checkOut);
        return occupied < hotel.getTotalRooms();
    }
}
