// src/main/java/com/initstore/initstorebackend/repository/CategoryRepository.java
package com.initstore.initstorebackend.repository;

import com.initstore.initstorebackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}