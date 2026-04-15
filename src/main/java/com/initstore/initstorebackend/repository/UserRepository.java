// src/main/java/com/initstore/initstorebackend/repository/UserRepository.java
package com.initstore.initstorebackend.repository;

import com.initstore.initstorebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // This method will help us check if an email is already in use
    Optional<User> findByEmail(String email);
}