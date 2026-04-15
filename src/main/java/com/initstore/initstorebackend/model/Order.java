package com.initstore.initstorebackend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; 
    
    private String userName; 
    
    @Column(columnDefinition = "TEXT")
    private String itemsSummary;
    
    // ✅ FIX: Changed to Double to match JSON number from React
    // BigDecimal causes deserialization issues with plain JSON numbers
    private Double totalAmount;
    
    private String shippingAddress;
    
    @Column(nullable = false)
    private String status = "Processing";
    
    private LocalDateTime orderDate;

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (status == null) {
            status = "Processing";
        }
    }

    // --- Getters and Setters ---
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public Long getUserId() { 
        return userId; 
    }
    
    public void setUserId(Long userId) { 
        this.userId = userId; 
    }
    
    public String getUserName() { 
        return userName; 
    }
    
    public void setUserName(String userName) { 
        this.userName = userName; 
    }
    
    public String getItemsSummary() { 
        return itemsSummary; 
    }
    
    public void setItemsSummary(String itemsSummary) { 
        this.itemsSummary = itemsSummary; 
    }
    
    public Double getTotalAmount() { 
        return totalAmount; 
    }
    
    public void setTotalAmount(Double totalAmount) { 
        this.totalAmount = totalAmount; 
    }
    
    public String getShippingAddress() { 
        return shippingAddress; 
    }
    
    public void setShippingAddress(String shippingAddress) { 
        this.shippingAddress = shippingAddress; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public LocalDateTime getOrderDate() { 
        return orderDate; 
    }
    
    public void setOrderDate(LocalDateTime orderDate) { 
        this.orderDate = orderDate; 
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
}