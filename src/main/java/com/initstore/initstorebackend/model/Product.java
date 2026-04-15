package com.initstore.initstorebackend.model;

import java.math.BigDecimal; // Use BigDecimal for precise currency handling

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a Product entity.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    // ⚠️ Updated to BigDecimal for monetary precision
    private BigDecimal price; 
    
    private String imageUrl;
    private int rating;

    // --- E-COMMERCE FILTERING FIELDS ADDED ---
    private boolean isNew;
    private boolean isSale;
    // ------------------------------------------

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // --- Constructors ---
    public Product() {
        // Default constructor required by JPA
    }

    // You might want a constructor for creating new objects
    public Product(String name, BigDecimal price, String imageUrl, Category category) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.isNew = true; // Default to new when created
        this.isSale = false;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // ⚠️ Updated to BigDecimal
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    // --- Getters/Setters for new fields ---
    public boolean getIsNew() { return isNew; } // JPA/Jackson will typically look for 'isNew()' but 'getIsNew()' is safer
    public void setIsNew(boolean isNew) { this.isNew = isNew; }

    public boolean getIsSale() { return isSale; }
    public void setIsSale(boolean isSale) { this.isSale = isSale; }
}