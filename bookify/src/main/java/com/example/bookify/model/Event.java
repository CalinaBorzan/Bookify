package com.example.bookify.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Entity class representing an event listing, extending the Listing class.
 * Represents specific details for an event listing, including venue, event date, and ticket capacity.
 */
@Entity
@Table(name="events")
public class Event extends Listing {

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable=false)
    private Integer ticketCapacity;

    /**
     * Constructs an Event instance with specified attributes.
     *
     * @param id The unique identifier of the event listing.
     * @param title The title of the event listing.
     * @param description The description of the event listing.
     * @param price The price of the event listing.
     * @param listingType The type of the event listing (e.g., HOTEL, FLIGHT, EVENT).
     * @param country The country where the event is located.
     * @param venue The venue where the event takes place.
     * @param eventDate The date and time of the event.
     * @param ticketCapacity The capacity of tickets available for the event.
     */
    public Event(Long id,
                 String title,
                 String description,
                 BigDecimal price,
                 ListingType listingType,
                 String country,
                 String venue,
                 LocalDateTime eventDate,
                 Integer ticketCapacity) {
        super(id, title, description, price, listingType, country);
        this.venue          = venue;
        this.eventDate      = eventDate;
        this.ticketCapacity = ticketCapacity;
    }

    /**
     * Default constructor for Event.
     */
    public Event() {
        super();

    }

    // Getters
    public String getVenue()               { return venue; }
    public LocalDateTime getEventDate()    { return eventDate; }

    // Setters
    public void setVenue(String venue)               { this.venue = venue; }
    public void setEventDate(LocalDateTime eventDate){ this.eventDate = eventDate; }
    public Integer getTicketCapacity() {
        return ticketCapacity;
    }
    public void setTicketCapacity(Integer ticketCapacity) {
        this.ticketCapacity = ticketCapacity;
    }
}
