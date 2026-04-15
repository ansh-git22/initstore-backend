package com.initstore.initstorebackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.initstore.initstorebackend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ✅ New Arrivals
    List<Product> findByIsNewTrue();
    
    // ✅ Sale Items
    List<Product> findByIsSaleTrue();
    
    // ✅ Filter by Category Name (old method - keep if needed)
    List<Product> findByCategory_NameIgnoreCase(String categoryName);

    // 🔥 IMPORTANT: Filter by Category ID (BEST METHOD)
    List<Product> findByCategory_Id(Long id);
}