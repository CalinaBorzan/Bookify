package com.example.bookify.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Abstract entity representing a listing in the system.
 * This class serves as a base class for different types of listings,
 * such as hotels, flights, and events.
 */
@Entity
@Table(name="listings")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "createdBy"})
public abstract class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType;
    @Column(nullable=false)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = true)
    private User createdBy;


    public Listing() {
    }


    /**
     * Constructs a Listing instance with specified attributes.
     *
     * @param id The unique identifier of the listing.
     * @param title The title of the listing.
     * @param description The description of the listing.
     * @param price The price of the listing.
     * @param listingType The type of the listing (e.g., HOTEL, FLIGHT, EVENT).
     * @param createdBy The user who created or owns the listing.
     * @param country The country associated with the listing.
     */
    public Listing(Long id,
                   String title,
                   String description,
                   BigDecimal price,
                   ListingType listingType,
                   User createdBy,String country) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.listingType = listingType;
        this.createdBy = createdBy;
        this.country = country;
    }

    /**
     * Constructs a Listing instance with specified attributes.
     * Does not include createdBy parameter.
     *
     * @param id The unique identifier of the listing.
     * @param title The title of the listing.
     * @param description The description of the listing.
     * @param price The price of the listing.
     * @param listingType The type of the listing (e.g., HOTEL, FLIGHT, EVENT).
     * @param country The country associated with the listing.
     */
    public Listing(Long id,
                   String title,
                   String description,
                   BigDecimal price,
                   ListingType listingType,String country) {
        this(id, title, description, price, listingType, null, country);
    }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
