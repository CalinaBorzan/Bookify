package com.example.bookify.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing a hotel listing, extending the Listing class.
 * Represents specific details for a hotel listing, including address, city,
 * star rating, total rooms, available dates, and other inherited attributes.
 */
@Entity
@Table(name="hotels")
public class Hotel extends Listing {
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    private Integer starRating;

    @Column(nullable = false)
    private Integer totalRooms;

    @Column(nullable = false)
    private LocalDate availableFrom;

    @Column(nullable = false)
    private LocalDate availableTo;


    /**
     * Default constructor for Hotel.
     */
    public Hotel() {}

    /**
     * Constructs a Hotel instance with specified attributes.
     *
     * @param id The unique identifier of the hotel listing.
     * @param title The title of the hotel listing.
     * @param description The description of the hotel listing.
     * @param price The price of the hotel listing.
     * @param listingType The type of the hotel listing (e.g., HOTEL, FLIGHT, EVENT).
     * @param createdBy The user who created or owns the hotel listing.
     * @param country The country where the hotel is located.
     * @param address The address of the hotel.
     * @param city The city where the hotel is located.
     * @param starRating The star rating of the hotel.
     * @param totalRooms The total number of rooms available in the hotel.
     * @param availableFrom The date from which the hotel is available.
     * @param availableTo The date until which the hotel is available.
     */
    public Hotel(Long id,
                 String title,
                 String description,
                 BigDecimal price,
                 ListingType listingType,
                 User createdBy,         // if you want to set the owner here
                 String country,         // NEW param
                 String address,
                 String city,
                 Integer starRating,
                 Integer totalRooms,
                 LocalDate availableFrom,
                 LocalDate availableTo) {
        super(id, title, description, price, listingType, createdBy, country);

        this.address       = address;
        this.city          = city;
        this.starRating    = starRating;
        this.totalRooms    = totalRooms;
        this.availableFrom = availableFrom;
        this.availableTo   = availableTo;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }
    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDate getAvailableTo() {
        return availableTo;
    }
    public void setAvailableTo(LocalDate availableTo) {
        this.availableTo = availableTo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }
    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }
}
