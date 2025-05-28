package com.example.bookify.service;

import com.example.bookify.dto.EventDTO;
import com.example.bookify.model.Event;
import com.example.bookify.model.ListingType;
import com.example.bookify.model.User;
import com.example.bookify.repository.EventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * EventService manages operations related to events, including creation, retrieval, updating, and deletion of event listings.
 */
@Service
public class EventService {
    private final EventRepository repo;
    private final UserService userService;

    /**
     * Constructs a new EventService instance with required dependencies.
     *
     * @param repo the EventRepository for accessing event data
     * @param userService the UserService for user-related operations
     */
    public EventService(EventRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }


    /**
     * Creates a new event based on the provided DTO.
     * Associates the event with the currently authenticated user as the creator.
     *
     * @param dto the EventDTO containing event details
     * @return the created Event entity
     * @throws RuntimeException if the authenticated user is not found
     */
    @Transactional
    public Event create(EventDTO dto) {
        // 1) Determine current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User creator = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + username));

        // 2) Build Event
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setPrice(dto.getPrice());
        event.setCountry(dto.getCountry());
        event.setListingType(ListingType.EVENT);
        event.setVenue(dto.getVenue());
        event.setEventDate(dto.getEventDate());
        event.setTicketCapacity(dto.getTicketCapacity());
        event.setCreatedBy(creator);

        return repo.save(event);
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the unique identifier of the event
     * @return the Event entity associated with the given ID
     * @throws RuntimeException if no event is found with the specified ID
     */
    public Event getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }

    /**
     * Retrieves all events in the system.
     *
     * @return a list of all Event entities
     */
    public List<Event> getAll() {
        return repo.findAll();
    }

    /**
     * Updates an existing event based on the provided DTO.
     * All fields in the DTO replace the existing event's fields.
     *
     * @param id the unique identifier of the event to update
     * @param dto the EventDTO containing updated event details
     * @return the updated Event entity
     * @throws RuntimeException if no event is found with the specified ID
     */
    @Transactional
    public Event update(Long id, EventDTO dto) {
        Event e = getById(id);
        e.setTitle(dto.getTitle());
        e.setDescription(dto.getDescription());
        e.setPrice(dto.getPrice());
        e.setVenue(dto.getVenue());
        e.setCountry(dto.getCountry());
        e.setEventDate(dto.getEventDate());
        e.setTicketCapacity(dto.getTicketCapacity());
        return repo.save(e);
    }

    /**
     * Deletes an event from the system by its unique identifier.
     *
     * @param id the unique identifier of the event to delete
     * @throws RuntimeException if no event is found with the specified ID
     */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Event not found: " + id);
        }
        repo.deleteById(id);
    }
}
