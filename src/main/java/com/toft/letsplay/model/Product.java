package com.toft.letsplay.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @NotBlank(message = "Product name can't be empty")
    @Size(min=1, max = 50)
    private String name;

    @NotBlank(message = "Product description can't be empty")
    @Size(min = 2, max = 150)
    private String description;

    @Positive(message = "Product price can't be negative")
    private Double price;

    @NotBlank(message = "Product userId can't be empty")
    private String userId;


    public Product() {}

    public Product(String id, String name, String description, double price, String userId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.userId = userId;
    }

    // Probably needs reworking later...
    public String getId() { return id;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() {return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}