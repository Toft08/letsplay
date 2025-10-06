package com.toft.letsplay.controller;

import com.toft.letsplay.dto.ProductDto;
import com.toft.letsplay.exception.ResourceNotFoundException;
import com.toft.letsplay.model.Product;
import com.toft.letsplay.repository.ProductRepository;
import com.toft.letsplay.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto, Authentication authentication) {
        return productService.createProduct(productDto, authentication);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ProductDto updateProduct(@PathVariable String id, @Valid @RequestBody ProductDto productDto, Authentication authentication) {
        return productService.updateProduct(id, productDto, authentication);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteProduct(@PathVariable String id, Authentication authentication) {
        productService.deleteProduct(id, authentication);
    }

    @GetMapping("/my-products")
    @PreAuthorize("isAuthenticated()")
    public List<ProductDto> getMyProducts(Authentication authentication) {
        String userEmail = authentication.getName();
        return productService.getProductsByUser(userEmail);
    }
}
