package com.initstore.initstorebackend.Controller;

import com.initstore.initstorebackend.model.Product;
import com.initstore.initstorebackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // ------------------------------------------------------------------
    // 1. POST Endpoint: CREATE New Product (ADMIN ONLY)
    // URL: POST /api/products
    // ------------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getPrice() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        // Ensures the entity is treated as a NEW record 
        if (product.getId() != null) {
            product.setId(null);
        }
        
        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // ------------------------------------------------------------------
    // 2. GET Endpoint: FETCH All Products (PUBLIC)
    // URL: GET /api/products
    // ------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ------------------------------------------------------------------
    // 3. GET Endpoint: FETCH New Arrivals (PUBLIC)
    // URL: GET /api/products/new
    // ------------------------------------------------------------------
    @GetMapping("/new")
    public ResponseEntity<List<Product>> getNewArrivals() {
        try {
            List<Product> products = productRepository.findByIsNewTrue();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ------------------------------------------------------------------
    // 4. GET Endpoint: FETCH Sale Items (PUBLIC)
    // URL: GET /api/products/sale
    // ------------------------------------------------------------------
    @GetMapping("/sale")
    public ResponseEntity<List<Product>> getSaleItems() {
        try {
            List<Product> products = productRepository.findByIsSaleTrue();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------------------------------------------------------
    // 5. GET Endpoint: FETCH By Category Name (PUBLIC)
    // URL: GET /api/products/category/tops
    // ------------------------------------------------------------------
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryName) {
        try {
            List<Product> products = productRepository.findByCategory_NameIgnoreCase(categoryName);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------------------------------------------------------
    // 6. PUT Endpoint: UPDATE Existing Product (ADMIN ONLY)
    // URL: PUT /api/products/{id}
    // ------------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        
        return productRepository.findById(id)
            .map(existingProduct -> {
                if (productDetails.getName() == null || productDetails.getPrice() == null) {
                    return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
                }
                
                existingProduct.setName(productDetails.getName());
                existingProduct.setDescription(productDetails.getDescription());
                existingProduct.setPrice(productDetails.getPrice());
                existingProduct.setImageUrl(productDetails.getImageUrl());
                existingProduct.setRating(productDetails.getRating());
                existingProduct.setIsNew(productDetails.getIsNew());
                existingProduct.setIsSale(productDetails.getIsSale());
                
                if (productDetails.getCategory() != null) {
                    existingProduct.setCategory(productDetails.getCategory());
                }

                Product updatedProduct = productRepository.save(existingProduct);
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK); 
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ------------------------------------------------------------------
    // 7. DELETE Endpoint: DELETE Product by ID (ADMIN ONLY)
    // URL: DELETE /api/products/{id}
    // ------------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        try {
            if (!productRepository.existsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            productRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        } catch (Exception e) {
            System.err.println("Error deleting product with ID " + id + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }
    
    // ------------------------------------------------------------------
    // 8. GET Endpoint: FETCH Single Product by ID (PUBLIC)
    // URL: GET /api/products/{id}
    // ------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
            .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}