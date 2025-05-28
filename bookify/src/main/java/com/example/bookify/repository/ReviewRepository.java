package com.example.bookify.repository;

import com.example.bookify.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Book;
import java.util.List;

/**
 * Repository interface for accessing Review entities in the database.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBooking_Listing_IdAndModeratedTrue(Long listingId);

    /* for “My Bookings” page */
    List<Review> findByBooking_Id(Long bookingId);
    List<Review> findByModeratedFalse();
    List<Review> findByModeratedFalseAndModerationRemarksIsNull();

    /* already used for /listing/{id} */
    List<Review> findByBooking_Listing_Id(Long listingId);

}
