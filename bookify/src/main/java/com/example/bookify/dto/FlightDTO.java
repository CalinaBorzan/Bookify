package com.example.bookify.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Data transfer object (DTO) for Flight listings.
 * Represents data transferred between client and server for Flight operations.
 */
public class FlightDTO {
    private String title, country;
    private String description;
    private BigDecimal price;
    private String airline;
    private String departure;
    private String arrival;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer seatCapacity;


    public FlightDTO() {}


    /**
     * Constructs a FlightDTO with specified attributes.
     *
     * @param title The title of the flight.
     * @param country The country where the flight is located.
     * @param description The description of the flight.
     * @param price The price of the flight.
     * @param airline The airline operating the flight.
     * @param departure The departure location of the flight.
     * @param arrival The arrival location of the flight.
     * @param departureTime The departure date and time of the flight.
     * @param arrivalTime The arrival date and time of the flight.
     * @param seatCapacity The capacity of seats available for the flight.
     */
    public FlightDTO(String title,String country,
                     String description,
                     BigDecimal price,
                     String airline,
                     String departure,
                     String arrival,
                     LocalDateTime departureTime,
                     LocalDateTime arrivalTime,Integer seatCapacity) {
        this.title         = title;
        this.country      = country;
        this.description   = description;
        this.price         = price;
        this.airline       = airline;
        this.departure     = departure;
        this.arrival       = arrival;
        this.departureTime = departureTime;
        this.arrivalTime   = arrivalTime;
        this.seatCapacity = seatCapacity;
    }

    // Getters
    public String getTitle()              { return title; }
    public String getDescription()        { return description; }
    public BigDecimal getPrice()          { return price; }
    public String getAirline()            { return airline; }
    public String getDeparture()          { return departure; }
    public String getArrival()            { return arrival; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime()   { return arrivalTime; }


    // Setters
    public void setTitle(String title)               { this.title = title; }
    public void setDescription(String description)   { this.description = description; }
    public void setPrice(BigDecimal price)           { this.price = price; }
    public void setAirline(String airline)           { this.airline = airline; }
    public void setDeparture(String departure)       { this.departure = departure; }
    public void setArrival(String arrival)           { this.arrival = arrival; }
    public void setDepartureTime(LocalDateTime dt)   { this.departureTime = dt; }
    public void setArrivalTime(LocalDateTime at)     { this.arrivalTime = at; }
    public void setSeatCapacity(Integer seatCapacity) {
        this.seatCapacity = seatCapacity;
    }
    public Integer getSeatCapacity() {
        return seatCapacity;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
