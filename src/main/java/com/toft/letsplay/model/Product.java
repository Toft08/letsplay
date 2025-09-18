package com.toft.letsplay.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @NotBlank(message = "Product name can't be empty")
    private String name;

    @NotBlank(message = "Product description can't be empty")
    private String description;

    @Positive(message = "Product price can't be negative")
    private Double price;

    @NotBlank(message = "Product userId can't be empty")
    private String userId;
}