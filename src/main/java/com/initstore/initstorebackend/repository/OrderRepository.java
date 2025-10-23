package com.initstore.initstorebackend.repository;

import com.initstore.initstorebackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by userId, sorted by orderDate in descending order (newest first)
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}