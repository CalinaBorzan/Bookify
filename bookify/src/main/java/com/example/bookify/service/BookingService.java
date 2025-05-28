package com.example.bookify.service;

import com.example.bookify.dto.BookingDTO;
import com.example.bookify.model.*;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.ListingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * BookingService manages booking-related operations, including creation, retrieval, updating, and cancellation of bookings.
 * It integrates with user, listing, email, and hotel availability services to facilitate booking processes.
 */
@Service
public class BookingService {
    private final BookingRepository repo;
    private final UserService userService;
    private final ListingRepository listingRepo;
    private final EmailService emailService;
    private final HotelAvailabilityService hotelAvailabilityService;


    /**
     * Constructs a new BookingService instance with required dependencies.
     *
     * @param repo the BookingRepository for accessing booking data
     * @param userService the UserService for user-related operations
     * @param listingRepo the ListingRepository for accessing listing data
     * @param emailService the EmailService for sending email notifications
     * @param hotelAvailabilityService the HotelAvailabilityService for checking hotel room availability
     */
    public BookingService(BookingRepository repo,
                          UserService userService,
                          ListingRepository listingRepo,
                          EmailService emailService,
                          HotelAvailabilityService hotelAvailabilityService) {
        this.repo         = repo;
        this.userService  = userService;
        this.listingRepo  = listingRepo;
        this.emailService = emailService;
        this.hotelAvailabilityService = hotelAvailabilityService;
    }

    /**
     * Creates a new booking based on the provided DTO.
     * Validates the user's authentication, listing availability, and creates a booking with optional payment processing.
     * Sends a confirmation email to the booking user.
     *
     * @param dto the BookingDTO containing booking details
     * @return the created Booking entity
     * @throws RuntimeException if the authenticated user or listing is not found, or if the listing is fully booked
     */
    @Transactional
    public Booking create(BookingDTO dto) {
        // 1) get current username from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2) look up the User entity
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 3) load the listing
        Listing listing = listingRepo.findById(dto.getListingId())
                .orElseThrow(() ->
                        new RuntimeException("Listing not found: " + dto.getListingId())
                );

        // 4) availability checks
        if (listing instanceof Hotel h) {
            long used = repo.countHotelOverlaps(
                    h.getId(), dto.getCheckIn(), dto.getCheckOut());
            if (used >= h.getTotalRooms())
                throw new RuntimeException("No rooms available...");
        }
        if (listing instanceof Flight f) {
            int booked = repo.sumFlightGuests(f.getId());
            if (booked + dto.getNumGuests() > f.getSeatCapacity())
                throw new RuntimeException("Not enough seats...");
        }
        if (listing instanceof Event e) {
            int sold = repo.sumEventGuests(e.getId());
            if (sold + dto.getNumGuests() > e.getTicketCapacity())
                throw new RuntimeException("Event sold out...");
        }

        // 5) build and save the booking
        Booking booking = Booking.builder()
                .user(user)
                .listing(listing)
                .type(dto.getType() != null
                        ? dto.getType()
                        : BookingType.valueOf(listing.getListingType().name()))
                .status(dto.getStatus() != null
                        ? dto.getStatus()
                        : BookingStatus.CONFIRMED)
                .bookedAt(dto.getBookedAt() != null
                        ? dto.getBookedAt()
                        : LocalDateTime.now())
                .checkIn(dto.getCheckIn())
                .checkOut(dto.getCheckOut())
                .numGuests(dto.getNumGuests())
                .build();

        Booking saved = repo.save(booking);

        // 6) decrement availability
        if (listing instanceof Hotel h2) {
            h2.setTotalRooms(h2.getTotalRooms() - 1);
            listingRepo.save(h2);
        } else if (listing instanceof Flight f2) {
            f2.setSeatCapacity(f2.getSeatCapacity() - dto.getNumGuests());
            listingRepo.save(f2);
        } else if (listing instanceof Event e2) {
            e2.setTicketCapacity(e2.getTicketCapacity() - dto.getNumGuests());
            listingRepo.save(e2);
        }

        // 7) record a pending payment if pay-now requested
        if (dto.isPayNow()) {
            Payment p = new Payment.Builder()
                    .booking(saved)
                    .amount(saved.getListing().getPrice())
                    .paidAt(LocalDateTime.now())
                    .status(PaymentStatus.SUCCESSFUL)
                    .transactionId(UUID.randomUUID().toString())
                    .build();
            // if you have a PaymentService, use that; otherwise persist directly:
            saved.getPayments().add(p);
            repo.save(saved);
        }

        // 8) send confirmation to the *booking* user
        emailService.sendBookingConfirmation(saved);

        return saved;
    }

    /**
     * Retrieves a booking by its unique identifier.
     *
     * @param id the unique identifier of the booking
     * @return the Booking entity associated with the given ID
     * @throws RuntimeException if no booking is found with the specified ID
     */
    public Booking getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
    }


    /**
     * Retrieves all bookings associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a list of Booking entities for the specified user
     */
    public List<Booking> getByUser(Long userId) {
        return repo.findByUserId(userId);
    }


    /**
     * Cancels a booking by setting its status to CANCELLED.
     *
     * @param id the unique identifier of the booking to cancel
     * @return the updated Booking entity with CANCELLED status
     * @throws RuntimeException if no booking is found with the specified ID
     */
    @Transactional
    public Booking cancel(Long id) {
        Booking booking = getById(id);
        booking.setStatus(BookingStatus.CANCELLED);
        return repo.save(booking);
    }


    /**
     * Retrieves all bookings made by the currently authenticated user.
     *
     * @return a list of Booking entities for the current user
     * @throws RuntimeException if no authenticated user is found or if the user does not exist
     */
    public List<Booking> getByCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User current = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return repo.findByUserId(current.getId());
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the User entity of the authenticated user
     * @throws RuntimeException if no authenticated user is found or if the user does not exist
     */
    private User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Retrieves a list of BookingDTOs for all bookings made by the currently authenticated user.
     *
     * @return a list of BookingDTO objects containing booking details for the current user
     * @throws RuntimeException if no authenticated user is found or if the user does not exist
     */
    public List<BookingDTO> getDtosForCurrentUser() {

        User me = getCurrentAuthenticatedUser();
        List<Booking> entities = repo.findByUserId(me.getId());

        return entities.stream()
                .map(b -> BookingDTO.builder()
                        .id           (b.getId())
                        .userId       (me.getId())
                        .listingId    (b.getListing().getId())
                        .listingTitle (b.getListing().getTitle())
                        .listingPrice (b.getListing().getPrice())
                        .type         (b.getType())
                        .status       (b.getStatus())
                        .bookedAt     (b.getBookedAt())
                        .checkIn      (b.getCheckIn())
                        .checkOut     (b.getCheckOut())
                        .numGuests    (b.getNumGuests())
                        .payNow       (false)
                        .build())
                .toList();
    }

    /**
     * Cancels a booking by setting its status to CANCELLED and returns the updated booking as a DTO.
     *
     * @param id the unique identifier of the booking to cancel
     * @return a BookingDTO representing the cancelled booking
     * @throws RuntimeException if no booking is found with the specified ID
     */
    @Transactional
    public BookingDTO cancelAsDto(Long id) {
        Booking booking = getById(id);
        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = repo.save(booking);

        return BookingDTO.builder()
                .id           (saved.getId())
                .userId       (saved.getUser().getId())
                .listingId    (saved.getListing().getId())
                .listingTitle (saved.getListing().getTitle())
                .listingPrice (saved.getListing().getPrice())
                .type         (saved.getType())
                .status       (saved.getStatus())
                .bookedAt     (saved.getBookedAt())
                .checkIn      (saved.getCheckIn())
                .checkOut     (saved.getCheckOut())
                .numGuests    (saved.getNumGuests())
                .payNow       (false)
                .build();
    }

    /**
     * Retrieves all bookings in the system.
     *
     * @return a list of all Booking entities
     */
    public List<Booking> getAll() {
        return repo.findAll();
    }

    /**
     * Updates an existing booking based on the provided DTO.
     * Only fields provided in the DTO are updated, while unspecified fields retain their existing values.
     *
     * @param id the unique identifier of the booking to update
     * @param dto the BookingDTO containing updated booking details
     * @return the updated Booking entity
     * @throws RuntimeException if no booking is found with the specified ID
     */
    @Transactional
    public Booking update(Long id, BookingDTO dto) {
        Booking booking = getById(id);
        if (dto.getStatus() != null) booking.setStatus(dto.getStatus());
        if (dto.getCheckIn() != null) booking.setCheckIn(dto.getCheckIn());
        if (dto.getCheckOut() != null) booking.setCheckOut(dto.getCheckOut());
        if (dto.getNumGuests() != null) booking.setNumGuests(dto.getNumGuests());
        return repo.save(booking);
    }

    /**
     * Deletes a booking from the system by its unique identifier.
     *
     * @param id the unique identifier of the booking to delete
     * @throws RuntimeException if no booking is found with the specified ID
     */
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the User entity of the authenticated user
     * @throws RuntimeException if no authenticated user is found or if the user does not exist
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + auth.getName()));
    }


    /**
     * Retrieves all bookings for listings owned by the currently authenticated user.
     *
     * @return a list of Booking entities for listings owned by the current user
     * @throws RuntimeException if no authenticated user is found or if the user does not exist
     */
    public List<Booking> getByListingsOfCurrentUser() {
        User me = getCurrentUser();
        return repo.findByListingOwnerId(me.getId());
    }


}
