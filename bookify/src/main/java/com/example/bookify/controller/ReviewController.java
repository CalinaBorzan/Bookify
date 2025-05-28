package com.example.bookify.controller;


import com.example.bookify.dto.ReviewDTO;
import com.example.bookify.model.Review;
import com.example.bookify.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controller class for managing review-related operations.
 * Provides endpoints for creating, retrieving, moderating, and listing reviews.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService svc;

    /**
     * Constructs an instance of ReviewController with the specified ReviewService.
     *
     * @param svc The ReviewService used for review operations.
     */
    public ReviewController(ReviewService svc) {
        this.svc = svc;
    }


    /**
     * Creates a new review based on the provided ReviewDTO.
     *
     * @param dto The ReviewDTO object containing review details.
     * @return ResponseEntity with HTTP status 201 CREATED and the created Review object.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewDTO dto) {
        Review created = svc.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param id The ID of the review to retrieve.
     * @return ResponseEntity with the Review object with the specified ID.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(svc.getById(id));

    }


    /**
     * Moderates a review by approving or rejecting it with optional remarks.
     *
     * @param id       The ID of the review to moderate.
     * @param approved Boolean indicating whether the review is approved.
     * @param remarks  Optional remarks for moderation decision.
     * @return ResponseEntity with the moderated Review object.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/moderate")
    public ResponseEntity<Review> moderateReview(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks) {
        Review moderated = svc.moderate(id, approved, remarks);
        return ResponseEntity.ok(moderated);
    }


    /**
     * Retrieves all reviews associated with a specific booking.
     *
     * @param bookingId The ID of the booking to retrieve reviews for.
     * @return ResponseEntity with the list of reviews associated with the specified booking.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Review>> byBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(svc.getByBookingId(bookingId));
    }

    /**
     * Retrieves all reviews associated with a specific listing.
     *
     * @param listingId The ID of the listing to retrieve reviews for.
     * @return ResponseEntity with the list of reviews associated with the specified listing.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<Review>> byListing(@PathVariable Long listingId) {
        return ResponseEntity.ok(svc.getByListing(listingId));
    }


    /**
     * Retrieves all pending reviews that require moderation.
     *
     * @return ResponseEntity with the list of pending reviews.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<Review>> pending() {
        List<Review> list = svc.getPending();
        return ResponseEntity.ok(list);
    }

}
