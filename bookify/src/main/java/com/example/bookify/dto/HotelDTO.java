package com.example.bookify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data transfer object (DTO) for Hotel listings.
 * Represents data transferred between client and server for Hotel operations.
 */
public class HotelDTO {
    private String title;
    private String country;
    private String description;
    private BigDecimal price;
    private String address;
    private String city;
    private Integer starRating;
    private Integer totalRooms;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate availableFrom, availableTo;

    public HotelDTO() {}


    /**
     * Constructs a HotelDTO with specified attributes.
     *
     * @param title The title of the hotel.
     * @param description The description of the hotel.
     * @param country The country where the hotel is located.
     * @param price The price of the hotel.
     * @param address The address of the hotel.
     * @param city The city where the hotel is located.
     * @param starRating The star rating of the hotel.
     * @param totalRooms The total number of rooms available in the hotel.
     * @param availableFrom The date from which the hotel is available.
     * @param availableTo The date until which the hotel is available.
     */
    public HotelDTO(String title,
                    String description,
                    String country,
                    BigDecimal price,
                    String address,
                    String city,
                    Integer starRating,
                    Integer totalRooms,
                    LocalDate availableFrom,
                    LocalDate availableTo) {
        this.title         = title;
        this.description   = description;
        this.price         = price;
        this.address       = address;
        this.city          = city;
        this.starRating    = starRating;
        this.totalRooms    = totalRooms;
        this.availableFrom = availableFrom;
        this.availableTo   = availableTo;
        this.country      = country;
    }

    public String getTitle()         { return title; }
    public String getDescription()   { return description; }
    public BigDecimal getPrice()     { return price; }
    public String getAddress()       { return address; }
    public String getCity()          { return city; }
    public Integer getStarRating()   { return starRating; }
    public Integer getTotalRooms() { return totalRooms; }
    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }
    public LocalDate getAvailableTo()   { return availableTo; }
    public void setAvailableTo(LocalDate availableTo) {
        this.availableTo = availableTo;
    }
    public String getCountry() {
        return country;
    }

    public void setTitle(String title)             { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price)         { this.price = price; }
    public void setAddress(String address)         { this.address = address; }
    public void setCity(String city)               { this.city = city; }
    public void setStarRating(Integer starRating)  { this.starRating = starRating; }
    public void setCountry(String country) {
        this.country = country;
    }


}
