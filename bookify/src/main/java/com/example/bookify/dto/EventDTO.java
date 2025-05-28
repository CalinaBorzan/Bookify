package com.example.bookify.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data transfer object (DTO) for Event listings.
 * Represents data transferred between client and server for Event operations.
 */
public class EventDTO {
    private String title;
    private String country;
    private String description;
    private BigDecimal price;
    private String venue;
    private LocalDateTime eventDate;
    private Integer ticketCapacity;


    public EventDTO() {}


    /**
     * Constructs an EventDTO with specified attributes.
     *
     * @param title The title of the event.
     * @param description The description of the event.
     * @param country The country where the event takes place.
     * @param price The price of the event.
     * @param venue The venue of the event.
     * @param eventDate The date and time of the event.
     * @param ticketCapacity The capacity of tickets available for the event.
     */
    public EventDTO(String title,
                    String description,String country,
                    BigDecimal price,
                    String venue,
                    LocalDateTime eventDate,Integer ticketCapacity) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.venue = venue;
        this.eventDate = eventDate;
        this.ticketCapacity = ticketCapacity;
        this.country = country;

    }

    // Getters
    public String getTitle()               { return title; }
    public String getDescription()         { return description; }
    public BigDecimal getPrice()           { return price; }
    public String getVenue()               { return venue; }
    public LocalDateTime getEventDate()    { return eventDate; }

    // Setters
    public void setTitle(String title)               { this.title = title; }
    public void setDescription(String description)   { this.description = description; }
    public void setPrice(BigDecimal price)           { this.price = price; }
    public void setVenue(String venue)               { this.venue = venue; }
    public void setEventDate(LocalDateTime eventDate){ this.eventDate = eventDate; }
    public void setTicketCapacity(Integer ticketCapacity){ this.ticketCapacity = ticketCapacity; }
    public Integer getTicketCapacity(){ return ticketCapacity; }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
}
