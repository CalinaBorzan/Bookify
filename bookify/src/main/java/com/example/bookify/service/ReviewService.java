package com.example.bookify.service;


import com.example.bookify.dto.ReviewDTO;
import com.example.bookify.model.*;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReviewService manages operations related to reviews, including creation, retrieval, and moderation of review records.
 */
@Service
public class ReviewService {
    private final ReviewRepository repo;
    private final BookingRepository bookingRepo;

    /**
     * Constructs a new ReviewService instance with required dependencies.
     *
     * @param repo the ReviewRepository for accessing review data
     * @param bookingRepo the BookingRepository for accessing booking data
     */
    public ReviewService(ReviewRepository repo,
                         BookingRepository bookingRepo) {
        this.repo = repo;
        this.bookingRepo = bookingRepo;
    }


    /**
     * Creates a new review based on the provided DTO and associates it with a booking.
     * The review is initially marked as not moderated with no moderation remarks.
     *
     * @param dto the ReviewDTO containing review details
     * @return the created Review entity
     * @throws RuntimeException if the associated booking is not found
     */
    @Transactional
    public Review create(ReviewDTO dto) {
        Booking booking = bookingRepo.findById(dto.getBookingId())
                .orElseThrow(() ->
                        new RuntimeException("Booking not found: " + dto.getBookingId())
                );

        Review review = new Review();
        review.setBooking(booking);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setModerated(false);
        review.setModerationRemarks(null);

        return repo.save(review);
    }

    /**
     * Retrieves a review by its unique identifier.
     *
     * @param id the unique identifier of the review
     * @return the Review entity associated with the given ID
     * @throws RuntimeException if no review is found with the specified ID
     */
    public Review getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found: " + id));
    }


    /**
     * Retrieves all reviews associated with a specific booking.
     *
     * @param bookingId the unique identifier of the booking
     * @return a list of Review entities for the specified booking
     */
    public List<Review> getByBookingId(Long bookingId) {
        return repo.findByBooking_Id(bookingId);
    }


    /**
     * Retrieves all moderated reviews associated with a specific listing.
     *
     * @param listingId the unique identifier of the listing
     * @return a list of moderated Review entities for the specified listing
     */
    public List<Review> getByListing(Long listingId) {
        return repo.findByBooking_Listing_IdAndModeratedTrue(listingId);
    }


    /**
     * Retrieves all pending reviews that have not been moderated and have no moderation remarks.
     *
     * @return a list of pending Review entities
     */
    public List<Review> getPending() {
        return repo.findByModeratedFalseAndModerationRemarksIsNull();
    }


    /**
     * Moderates a review by updating its moderation status and remarks.
     *
     * @param id the unique identifier of the review to moderate
     * @param moderated the moderation status to set (true for approved, false for not moderated)
     * @param remarks the moderation remarks to set
     * @return the updated Review entity
     * @throws EntityNotFoundException if no review is found with the specified ID
     */
    public Review moderate(Long id, boolean moderated, String remarks) {
        Review r = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found "+id));
        r.setModerated(moderated);
        r.setModerationRemarks(remarks);
        return repo.save(r);
    }


}
