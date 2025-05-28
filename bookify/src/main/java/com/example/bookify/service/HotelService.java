package com.example.bookify.service;

import com.example.bookify.dto.HotelDTO;
import com.example.bookify.model.Hotel;
import com.example.bookify.model.ListingType;
import com.example.bookify.model.User;
import com.example.bookify.repository.HotelRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * HotelService manages operations related to hotel listings, including creation, retrieval, updating, and deletion.
 */
@Service
public class HotelService {
    private final HotelRepository repo;
    private final UserService userService;


    /**
     * Constructs a new HotelService instance with required dependencies.
     *
     * @param repo the HotelRepository for accessing hotel data
     * @param userService the UserService for user-related operations
     */
    public HotelService(HotelRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    /**
     * Creates a new hotel listing based on the provided DTO and associates it with the currently authenticated user as the creator.
     *
     * @param dto the HotelDTO containing hotel details
     * @return the created Hotel entity
     * @throws RuntimeException if the authenticated user is not found
     */
    @Transactional
    public Hotel create(HotelDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();                            // from UserDetails.getUsername()
        User creator = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Hotel h = new Hotel();
        h.setTitle(dto.getTitle());
        h.setDescription(dto.getDescription());
        h.setPrice(dto.getPrice());
        h.setListingType(ListingType.HOTEL);
        h.setAddress(dto.getAddress());
        h.setCity(dto.getCity());
        h.setStarRating(dto.getStarRating());
        h.setTotalRooms(dto.getTotalRooms());
        h.setCountry(dto.getCountry());                  // ← set country!

        // ← these two lines:
        h.setAvailableFrom(dto.getAvailableFrom());
        h.setAvailableTo(dto.getAvailableTo());

        h.setCreatedBy(creator);

        return repo.save(h);
    }

    /**
     * Retrieves a hotel by its unique identifier.
     *
     * @param id the unique identifier of the hotel
     * @return the Hotel entity associated with the given ID
     * @throws RuntimeException if no hotel is found with the specified ID
     */
    public Hotel getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found: " + id));
    }

    /**
     * Retrieves all hotel listings in the system.
     *
     * @return a list of all Hotel entities
     */
    public List<Hotel> getAll() {
        return repo.findAll();
    }

    /**
     * Updates an existing hotel listing based on the provided DTO.
     * All fields in the DTO replace the existing hotel's fields.
     *
     * @param id the unique identifier of the hotel to update
     * @param dto the HotelDTO containing updated hotel details
     * @return the updated Hotel entity
     * @throws RuntimeException if no hotel is found with the specified ID
     */
    @Transactional
    public Hotel update(Long id, HotelDTO dto) {
        Hotel h = getById(id);
        h.setTitle(dto.getTitle());
        h.setDescription(dto.getDescription());
        h.setCountry(dto.getCountry());
        h.setPrice(dto.getPrice());
        h.setAddress(dto.getAddress());
        h.setCity(dto.getCity());
        h.setStarRating(dto.getStarRating());
        h.setTotalRooms(dto.getTotalRooms());
        h.setAvailableFrom(dto.getAvailableFrom());
        h.setAvailableTo(dto.getAvailableTo());
        return repo.save(h);
    }

    /**
     * Deletes a hotel listing from the system by its unique identifier.
     *
     * @param id the unique identifier of the hotel to delete
     * @throws RuntimeException if no hotel is found with the specified ID
     */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Hotel not found: " + id);
        }
        repo.deleteById(id);
    }
}
