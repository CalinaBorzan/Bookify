package com.example.bookify.controller;

import com.example.bookify.dto.BookingDTO;
import com.example.bookify.model.Booking;
import com.example.bookify.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Controller class for managing booking-related operations.
 * Provides endpoints for creating, retrieving, and managing bookings.
 */

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService svc;

    /**
     * Constructs an instance of BookingController with the specified BookingService.
     *
     * @param svc The BookingService implementation used for booking operations.
     */
    public BookingController(BookingService svc) {
        this.svc = svc;
    }


    /**
     * Creates a new booking based on the provided BookingDTO.
     *
     * @param dto The BookingDTO object containing booking details.
     * @return ResponseEntity with HTTP status 201 CREATED and the created Booking object.
     */

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody BookingDTO dto) {
        Booking b = svc.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(b);
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param id The ID of the booking to retrieve.
     * @return The Booking object with the specified ID.
     */


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public Booking get(@PathVariable Long id) {
        return svc.getById(id);
    }

    /**
     * Retrieves all bookings associated with a specific user.
     *
     * @param userId The ID of the user to retrieve bookings for.
     * @return List of bookings associated with the specified user.
     */

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public List<Booking> byUser(@PathVariable Long userId) {
        return svc.getByUser(userId);
    }

    /**
     * Cancels a booking and returns its details as a BookingDTO.
     *
     * @param id The ID of the booking to cancel.
     * @return The details of the canceled booking as a BookingDTO.
     */

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{id}/cancel")
    public BookingDTO cancel(@PathVariable Long id) {
        return svc.cancelAsDto(id);
    }

    /**
     * Retrieves all bookings associated with the current authenticated user.
     *
     * @return List of BookingDTOs representing bookings for the current user.
     */

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public List<BookingDTO> myBookings() {
        return svc.getDtosForCurrentUser();
    }

    /**
     * Retrieves all bookings associated with the listings of the current authenticated user.
     *
     * @return ResponseEntity with a list of BookingDTOs representing bookings for the listings of the current user.
     */

    @GetMapping("/admin/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> forMyListings() {
        List<Booking> bookings = svc.getByListingsOfCurrentUser();
        return ResponseEntity.ok(convertToDto(bookings));
    }

    private List<BookingDTO> convertToDto(List<Booking> bookings) {
        return bookings.stream().map(b -> BookingDTO.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .listingId(b.getListing().getId())
                .listingTitle(b.getListing().getTitle())
                .listingPrice(b.getListing().getPrice())
                .type(b.getType())
                .status(b.getStatus())
                .bookedAt(b.getBookedAt())
                .checkIn(b.getCheckIn())
                .checkOut(b.getCheckOut())
                .numGuests(b.getNumGuests())
                .payNow(false)
                .build()
        ).collect(Collectors.toList());
    }

    /**
     * Updates a booking identified by its ID with the details from the provided BookingDTO.
     *
     * @param id  The ID of the booking to update.
     * @param dto The BookingDTO object containing updated booking details.
     * @return ResponseEntity with HTTP status 200 OK and the updated Booking object.
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/bookings/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody BookingDTO dto) {
        return ResponseEntity.ok(svc.update(id, dto));
    }

    /**
     * Deletes a booking identified by its ID.
     *
     * @param id The ID of the booking to delete.
     * @return ResponseEntity with HTTP status 204 NO CONTENT.
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

}
