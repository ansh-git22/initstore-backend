package com.initstore.initstorebackend.repository;

import com.initstore.initstorebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Method for New Arrivals (matches the isNew field in Product.java)
    List<Product> findByIsNewTrue();
    
    // Method for Sale Items (matches the isSale field in Product.java)
    List<Product> findByIsSaleTrue();
    
    // Method for filtering by Category name (matches the category field and its name property)
    List<Product> findByCategory_NameIgnoreCase(String categoryName);
}