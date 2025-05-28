package com.example.bookify.repository;

import com.example.bookify.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for accessing Listing entities in the database.
 */
public interface ListingRepository extends JpaRepository<Listing, Long> {
}
