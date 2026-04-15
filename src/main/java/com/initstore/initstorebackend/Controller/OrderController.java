package com.initstore.initstorebackend.Controller;

import com.initstore.initstorebackend.model.Order;
import com.initstore.initstorebackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; 

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // --- Endpoint 1: POST (Create New Order - For Checkout) ---
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            // Enhanced validation and logging
            System.out.println("=== INCOMING ORDER REQUEST ===");
            System.out.println("User ID: " + order.getUserId());
            System.out.println("User Name: " + order.getUserName());
            System.out.println("Total Amount: " + order.getTotalAmount());
            System.out.println("Shipping Address: " + order.getShippingAddress());
            System.out.println("Items: " + order.getItemsSummary());
            System.out.println("Status: " + order.getStatus());
            
            // Validate required fields
            if (order.getUserId() == null) {
                System.err.println("❌ ERROR: userId is null!");
                return ResponseEntity
                    .badRequest()
                    .body("User ID is required");
            }
            
            if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
                System.err.println("❌ ERROR: Invalid total amount!");
                return ResponseEntity
                    .badRequest()
                    .body("Valid total amount is required");
            }
            
            if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
                System.err.println("❌ ERROR: Shipping address is empty!");
                return ResponseEntity
                    .badRequest()
                    .body("Shipping address is required");
            }
            
            // Save the order
            Order savedOrder = orderRepository.save(order);
            
            System.out.println("✅ Order saved successfully!");
            System.out.println("   Order ID: " + savedOrder.getId());
            System.out.println("   Order Date: " + savedOrder.getOrderDate());
            System.out.println("   Status: " + savedOrder.getStatus());
            System.out.println("================================");
            
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
            
        } catch (Exception e) {
            System.err.println("❌ EXCEPTION while saving order:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Type: " + e.getClass().getName());
            e.printStackTrace();
            
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing order: " + e.getMessage());
        }
    }

    // --- Endpoint 2: GET (Fetch Orders by User ID - For MyOrdersPage) ---
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        try {
            List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.err.println("Error fetching orders for user " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- Endpoint 3: PUT (Cancel Order by ID) ---
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Optional<Order> orderData = orderRepository.findById(id);

        if (orderData.isPresent()) {
            Order order = orderData.get();
            
            if ("Processing".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("Cancelled");
                Order updatedOrder = orderRepository.save(order);
                return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
            } else {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Order cannot be cancelled. Current status: " + order.getStatus());
            }
        } else {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Order not found");
        }
    }
    
    // --- Endpoint 4: GET (Fetch All Orders - For Admin) ---
    @GetMapping 
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // --- Endpoint 5: PUT (Update Order Status - For Admin) ---
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody String newStatusPayload) {
        Optional<Order> orderData = orderRepository.findById(id);

        if (orderData.isPresent()) {
            Order order = orderData.get();
            String newStatus = newStatusPayload.replace("\"", "").trim();
            order.setStatus(newStatus); 
            Order updatedOrder = orderRepository.save(order);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } else {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Order not found");
        }
    }
}