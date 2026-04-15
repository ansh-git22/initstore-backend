package com.initstore.initstorebackend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.initstore.initstorebackend.model.Product;
import com.initstore.initstorebackend.repository.ProductRepository;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // ------------------------------------------------------------------
    // 1. CREATE PRODUCT
    // ------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getPrice() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (product.getId() != null) {
            product.setId(null);
        }

        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // ------------------------------------------------------------------
    // 2. GET ALL PRODUCTS
    // ------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    // ------------------------------------------------------------------
    // 3. GET NEW PRODUCTS
    // ------------------------------------------------------------------
    @GetMapping("/new")
    public ResponseEntity<List<Product>> getNewArrivals() {
        return new ResponseEntity<>(productRepository.findByIsNewTrue(), HttpStatus.OK);
    }

    // ------------------------------------------------------------------
    // 4. GET SALE PRODUCTS
    // ------------------------------------------------------------------
    @GetMapping("/sale")
    public ResponseEntity<List<Product>> getSaleItems() {
        return new ResponseEntity<>(productRepository.findByIsSaleTrue(), HttpStatus.OK);
    }

    // ------------------------------------------------------------------
    // 5. GET PRODUCTS BY CATEGORY NAME (KEEP OLD)
    // ------------------------------------------------------------------
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryName) {
        List<Product> products = productRepository.findByCategory_NameIgnoreCase(categoryName);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ------------------------------------------------------------------
    // 🔥 6. GET PRODUCTS BY CATEGORY ID (NEW - IMPORTANT)
    // ------------------------------------------------------------------
    @GetMapping("/category/id/{id}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Long id) {
        List<Product> products = productRepository.findByCategory_Id(id);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ------------------------------------------------------------------
    // 7. UPDATE PRODUCT
    // ------------------------------------------------------------------
    @PutMapping("/{id}")
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

                // ✅ update category properly
                if (productDetails.getCategory() != null) {
                    existingProduct.setCategory(productDetails.getCategory());
                }

                Product updatedProduct = productRepository.save(existingProduct);
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ------------------------------------------------------------------
    // 8. DELETE PRODUCT
    // ------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        productRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ------------------------------------------------------------------
    // 9. GET PRODUCT BY ID
    // ------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
            .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}