package com.toft.letsplay.service;

import com.toft.letsplay.dto.ProductDto;
import com.toft.letsplay.exception.ForbiddenException;
import com.toft.letsplay.exception.ResourceNotFoundException;
import com.toft.letsplay.model.Product;
import com.toft.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
        productDto.setUser(userEmail);

        Product product = toEntity(productDto);
        product.setUser(userEmail);
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

    public List<ProductDto> getProductsByUser(String user) {
        return productRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private boolean canModifyProduct(Product product, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) { return true; }
        // Only admins or the owner of the product can modify it
        String productOwner = product.getUser();
        return productOwner.equals(currentUserEmail);
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setUser(product.getUser());
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setUser(dto.getUser());
        return product;
    }
}
