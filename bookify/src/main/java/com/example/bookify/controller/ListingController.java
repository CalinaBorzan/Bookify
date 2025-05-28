package com.example.bookify.controller;

import com.example.bookify.dto.HotelDTO;
import com.example.bookify.dto.FlightDTO;
import com.example.bookify.dto.EventDTO;
import com.example.bookify.model.Hotel;
import com.example.bookify.model.Flight;
import com.example.bookify.model.Event;
import com.example.bookify.service.HotelService;
import com.example.bookify.service.FlightService;
import com.example.bookify.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller class for managing listings such as hotels, flights, and events.
 * Provides endpoints for creating, retrieving, updating, and deleting listings.
 */
@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final HotelService hotelSvc;
    private final FlightService flightSvc;
    private final EventService eventSvc;

    /**
     * Constructs an instance of ListingController with the specified services.
     *
     * @param hotelSvc  The HotelService used for hotel-related operations.
     * @param flightSvc The FlightService used for flight-related operations.
     * @param eventSvc  The EventService used for event-related operations.
     */
    public ListingController(
            HotelService hotelSvc,
            FlightService flightSvc,
            EventService eventSvc) {
        this.hotelSvc = hotelSvc;
        this.flightSvc = flightSvc;
        this.eventSvc = eventSvc;
    }

    /**
     * Creates a new hotel listing based on the provided HotelDTO.
     *
     * @param dto The HotelDTO object containing hotel details.
     * @return ResponseEntity with HTTP status 201 CREATED and the created Hotel object.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hotels")
    public ResponseEntity<Hotel> createHotel(@RequestBody HotelDTO dto) {
        Hotel created = hotelSvc.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * Retrieves a specific hotel listing by its ID.
     *
     * @param id The ID of the hotel listing to retrieve.
     * @return ResponseEntity with the Hotel object corresponding to the provided ID.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/hotels/{id}")
    public ResponseEntity<Hotel> getHotel(@PathVariable Long id) {
        Hotel hotel = hotelSvc.getById(id);
        return ResponseEntity.ok(hotel);
    }

    /**
     * Updates an existing hotel listing with the provided details.
     *
     * @param id  The ID of the hotel listing to update.
     * @param dto The HotelDTO object containing updated hotel details.
     * @return ResponseEntity with the updated Hotel object.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotels/{id}")
    public ResponseEntity<Hotel> updateHotel(
            @PathVariable Long id,
            @RequestBody HotelDTO dto) {
        Hotel updated = hotelSvc.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Retrieves all hotel listings.
     *
     * @return ResponseEntity with a list of all Hotel objects.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> listHotels() {
        List<Hotel> hotels = hotelSvc.getAll();
        return ResponseEntity.ok(hotels);
    }

    /**
     * Deletes a hotel listing by its ID.
     *
     * @param id The ID of the hotel listing to delete.
     * @return ResponseEntity with HTTP status 204 NO CONTENT.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a new flight listing based on the provided FlightDTO.
     *
     * @param dto The FlightDTO object containing flight details.
     * @return ResponseEntity with HTTP status 201 CREATED and the created Flight object.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/flights")
    public ResponseEntity<Flight> createFlight(@RequestBody FlightDTO dto) {
        Flight created = flightSvc.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * Retrieves a specific flight listing by its ID.
     *
     * @param id The ID of the flight listing to retrieve.
     * @return ResponseEntity with the Flight object corresponding to the provided ID.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/flights/{id}")
    public ResponseEntity<Flight> getFlight(@PathVariable Long id) {
        Flight flight = flightSvc.getById(id);
        return ResponseEntity.ok(flight);
    }

    /**
     * Updates an existing flight listing with the provided details.
     *
     * @param id  The ID of the flight listing to update.
     * @param dto The FlightDTO object containing updated flight details.
     * @return ResponseEntity with the updated Flight object.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/flights/{id}")
    public ResponseEntity<Flight> updateFlight(
            @PathVariable Long id,
            @RequestBody FlightDTO dto) {
        Flight updated = flightSvc.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Retrieves all flight listings.
     *
     * @return ResponseEntity with a list of all Flight objects.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> listFlights() {
        List<Flight> flights = flightSvc.getAll();
        return ResponseEntity.ok(flights);
    }

    /**
     * Deletes a flight listing by its ID.
     *
     * @param id The ID of the flight listing to delete.
     * @return ResponseEntity with HTTP status 204 NO CONTENT.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/flights/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a new event listing based on the provided EventDTO.
     *
     * @param dto The EventDTO object containing event details.
     * @return ResponseEntity with HTTP status 201 CREATED and the created Event object.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO dto) {
        Event created = eventSvc.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * Retrieves a specific event listing by its ID.
     *
     * @param id The ID of the event listing to retrieve.
     * @return ResponseEntity with the Event object corresponding to the provided ID.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        Event event = eventSvc.getById(id);
        return ResponseEntity.ok(event);
    }

    /**
     * Updates an existing event listing with the provided details.
     *
     * @param id  The ID of the event listing to update.
     * @param dto The EventDTO object containing updated event details.
     * @return ResponseEntity with the updated Event object.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/events/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody EventDTO dto) {
        Event updated = eventSvc.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Retrieves all event listings.
     *
     * @return ResponseEntity with a list of all Event objects.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/events")
    public ResponseEntity<List<Event>> listEvents() {
        List<Event> events = eventSvc.getAll();
        return ResponseEntity.ok(events);
    }

    /**
     * Deletes an event listing by its ID.
     *
     * @param id The ID of the event listing to delete.
     * @return ResponseEntity with HTTP status 204 NO CONTENT.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventSvc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
