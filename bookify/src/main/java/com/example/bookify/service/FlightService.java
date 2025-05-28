package com.example.bookify.service;

import com.example.bookify.dto.FlightDTO;
import com.example.bookify.model.Flight;
import com.example.bookify.model.ListingType;
import com.example.bookify.model.User;
import com.example.bookify.repository.FlightRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FlightService manages operations related to flight listings, including creation, retrieval, updating, and deletion.
 */
@Service
public class FlightService {
    private final FlightRepository repo;
    private final UserService userService;

    /**
     * Constructs a new FlightService instance with required dependencies.
     *
     * @param repo the FlightRepository for accessing flight data
     * @param userService the UserService for user-related operations
     */
    public FlightService(FlightRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    /**
     * Creates a new flight listing based on the provided DTO and associates it with the currently authenticated user as the creator.
     *
     * @param dto the FlightDTO containing flight details
     * @return the created Flight entity
     * @throws RuntimeException if the authenticated user is not found
     */
    @Transactional
    public Flight create(FlightDTO dto) {
        // 1) Determine current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User creator = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + username));

        // 2) Build Flight
        Flight f = new Flight();
        f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setPrice(dto.getPrice());
        f.setListingType(ListingType.FLIGHT);
        f.setAirline(dto.getAirline());
        f.setDeparture(dto.getDeparture());
        f.setArrival(dto.getArrival());
        f.setCountry(dto.getCountry());

        f.setDepartureTime(dto.getDepartureTime());
        f.setArrivalTime(dto.getArrivalTime());
        f.setSeatCapacity(dto.getSeatCapacity());
        // ← link back to creator
        f.setCreatedBy(creator);

        return repo.save(f);
    }

    /**
     * Retrieves a flight by its unique identifier.
     *
     * @param id the unique identifier of the flight
     * @return the Flight entity associated with the given ID
     * @throws RuntimeException if no flight is found with the specified ID
     */
    public Flight getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found: " + id));
    }

    /**
     * Retrieves all flight listings in the system.
     *
     * @return a list of all Flight entities
     */
    public List<Flight> getAll() {
        return repo.findAll();
    }


    /**
     * Updates an existing flight listing based on the provided DTO.
     * All fields in the DTO replace the existing flight's fields.
     *
     * @param id the unique identifier of the flight to update
     * @param dto the FlightDTO containing updated flight details
     * @return the updated Flight entity
     * @throws RuntimeException if no flight is found with the specified ID
     */
    @Transactional
    public Flight update(Long id, FlightDTO dto) {
        Flight f = getById(id);
        f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setPrice(dto.getPrice());
        f.setAirline(dto.getAirline());
        f.setDeparture(dto.getDeparture());
        f.setCountry(dto.getCountry());
        f.setArrival(dto.getArrival());
        f.setDepartureTime(dto.getDepartureTime());
        f.setArrivalTime(dto.getArrivalTime());
        f.setSeatCapacity(dto.getSeatCapacity());
        return repo.save(f);
    }

    /**
     * Deletes a flight listing from the system by its unique identifier.
     *
     * @param id the unique identifier of the flight to delete
     * @throws RuntimeException if no flight is found with the specified ID
     */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Flight not found: " + id);
        }
        repo.deleteById(id);
    }
}
