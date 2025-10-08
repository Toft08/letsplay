package com.toft.letsplay.service;

import com.toft.letsplay.dto.UserDto;
import com.toft.letsplay.exception.BadRequestException;
import com.toft.letsplay.exception.ForbiddenException;
import com.toft.letsplay.exception.ResourceNotFoundException;
import com.toft.letsplay.model.User;
import com.toft.letsplay.repository.UserRepository;
import com.toft.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Helper method to get User from UserDetails
    private User getUserFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database"));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDto(user);
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this email: " + email));
        return toDto(user);
    }

    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new BadRequestException("User with email " + userDto.getEmail() + " already exists");
        }

        User user = toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public UserDto updateUser(String id, UserDto userDto, Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !user.getEmail().equals(currentUserEmail)) {
            throw new ForbiddenException("You can only update your own profile");
        }

        // Check if the new email is already taken by another user
        if (!user.getEmail().equals(userDto.getEmail())) {
            userRepository.findByEmail(userDto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(id)) {
                    throw new BadRequestException("Email " + userDto.getEmail() + " is already taken by another user");
                }
            });
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        // No one can change the role

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        User updated = userRepository.save(user);
        return toDto(updated);
    }

    @Transactional
    public void deleteUser(String id, UserDetails userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new ForbiddenException("Admin user cannot be deleted");
        }
        
        // Delete all products associated with this user first (by user ID)
        productRepository.deleteByUserId(id);
        
        // Then delete the user
        userRepository.deleteById(id);
    }

    // Helper methods
    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    private User toEntity(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPassword(dto.getPassword());
        return user;
    }
}
