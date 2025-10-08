package com.toft.letsplay.service;

import com.toft.letsplay.dto.ProductDto;
import com.toft.letsplay.exception.ForbiddenException;
import com.toft.letsplay.exception.ResourceNotFoundException;
import com.toft.letsplay.model.Product;
import com.toft.letsplay.model.User;
import com.toft.letsplay.repository.ProductRepository;
import com.toft.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to get User from UserDetails
    private User getUserFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database"));
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with this id:" + id));
        return toDto(product);
    }

    public ProductDto createProduct(ProductDto productDto, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database"));
        
        Product product = toEntity(productDto);
        product.setUserId(user.getId()); // Store user ID internally
        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    public ProductDto updateProduct(String id, ProductDto productDto, Authentication authentication) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with this id:" + id));

        if (!canModifyProduct(existing, authentication)) {
            throw new ForbiddenException("You don't have permission to modify this product");
        }

        existing.setName(productDto.getName());
        existing.setDescription(productDto.getDescription());
        existing.setPrice(productDto.getPrice());


        Product updated = productRepository.save(existing);
        return toDto(updated);
    }

    public void deleteProduct(String id, Authentication authentication) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with this id:" + id));

        if (!canModifyProduct(existing, authentication)) {
            throw new ForbiddenException("You don't have permission to modify this product");
        }

        productRepository.deleteById(id);
    }

    public List<ProductDto> getProductsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));
        
        return productRepository.findByUserId(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private boolean canModifyProduct(Product product, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) { return true; }
        
        // Get current user's ID to compare with product's userId
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database"));
        
        return product.getUserId().equals(currentUser.getId());
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        
        // Convert userId to email for display (don't expose user IDs)
        User user = userRepository.findById(product.getUserId())
                .orElse(null);
        dto.setUser(user != null ? user.getEmail() : "Unknown User");
        
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        // Note: userId should be set separately in service methods, not from DTO
        return product;
    }
}
