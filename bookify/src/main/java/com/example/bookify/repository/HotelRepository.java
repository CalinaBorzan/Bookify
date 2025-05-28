package com.example.bookify.repository;

import com.example.bookify.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing Hotel entities in the database.
 */
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
