package com.toft.letsplay.repository;

import com.toft.letsplay.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    // Need to figure out what to query

    // Optional<Product> findById(String id);
    List<Product> findByUserId(String userId);
}