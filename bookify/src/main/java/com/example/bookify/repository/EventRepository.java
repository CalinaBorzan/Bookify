package com.example.bookify.repository;

import com.example.bookify.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing Event entities in the database.
 */
public interface EventRepository extends JpaRepository<Event, Long> {
}
