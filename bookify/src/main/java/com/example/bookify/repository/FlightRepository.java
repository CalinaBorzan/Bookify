package com.example.bookify.repository;

import com.example.bookify.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing Flight entities in the database.
 */
public interface FlightRepository extends JpaRepository<Flight, Long> {
}
