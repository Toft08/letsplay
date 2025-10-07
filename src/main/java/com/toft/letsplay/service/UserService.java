package com.toft.letsplay.service;

import com.toft.letsplay.dto.UserDto;
import com.toft.letsplay.exception.BadRequestException;
import com.toft.letsplay.exception.ForbiddenException;
import com.toft.letsplay.exception.ResourceNotFoundException;
import com.toft.letsplay.model.User;
import com.toft.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        // No one can change the role

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        User updated = userRepository.save(user);
        return toDto(updated);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new ForbiddenException("Admin user cannot be deleted");
        }
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
