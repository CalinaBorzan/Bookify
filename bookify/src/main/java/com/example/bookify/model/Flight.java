package com.example.bookify.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Entity class representing a flight listing, extending the Listing class.
 * Represents specific details for a flight listing, including airline, departure,
 * arrival, departure time, arrival time, and seat capacity.
 */
@Entity
@Table(name="flights")
public class Flight extends Listing {

    @Column(nullable = false)
    private String airline;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String arrival;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable=false)
    private Integer seatCapacity;

    /**
     * Default constructor for Flight.
     */
    public Flight() {
        super();
    }

    /**
     * Constructs a Flight instance with specified attributes.
     *
     * @param id The unique identifier of the flight listing.
     * @param title The title of the flight listing.
     * @param description The description of the flight listing.
     * @param price The price of the flight listing.
     * @param listingType The type of the flight listing (e.g., HOTEL, FLIGHT, EVENT).
     * @param country The country where the flight departs from.
     * @param airline The airline operating the flight.
     * @param departure The departure location of the flight.
     * @param arrival The arrival location of the flight.
     * @param departureTime The date and time of departure for the flight.
     * @param arrivalTime The date and time of arrival for the flight.
     * @param seatCapacity The number of seats available on the flight.
     */
    public Flight(Long id,
                  String title,
                  String description,
                  BigDecimal price,
                  ListingType listingType,
                  String country,
                  String airline,
                  String departure,
                  String arrival,
                  LocalDateTime departureTime,
                  LocalDateTime arrivalTime,
                  Integer seatCapacity) {
        super(id, title, description, price, listingType, country);
        this.airline       = airline;
        this.departure     = departure;
        this.arrival       = arrival;
        this.departureTime = departureTime;
        this.arrivalTime   = arrivalTime;
        this.seatCapacity  = seatCapacity;
    }

    // Getters
    public String getAirline()            { return airline; }
    public String getDeparture()          { return departure; }
    public String getArrival()            { return arrival; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime()   { return arrivalTime; }

    // Setters
    public void setAirline(String airline)            { this.airline = airline; }
    public void setDeparture(String departure)        { this.departure = departure; }
    public void setArrival(String arrival)            { this.arrival = arrival; }
    public void setDepartureTime(LocalDateTime dt)    { this.departureTime = dt; }
    public void setArrivalTime(LocalDateTime at)      { this.arrivalTime = at; }

    public void setSeatCapacity(Integer seatCapacity) {
        this.seatCapacity = seatCapacity;
    }
    public Integer getSeatCapacity() {
        return seatCapacity;
    }

}
