package com.toft.letsplay.controller;

import com.toft.letsplay.dto.UserDto;
import com.toft.letsplay.exception.UnauthorizedException;
import com.toft.letsplay.model.User;
import com.toft.letsplay.repository.UserRepository;
import com.toft.letsplay.security.JwtUtil;
import com.toft.letsplay.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return Map.of("token", token);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody UserDto userDto) {
        if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
            userDto.setRole("USER");
        }

        if ("ADMIN".equals(userDto.getRole())) {
            userDto.setRole("USER");
        }

        UserDto createdUser = userService.createUser(userDto);
        String token = jwtUtil.generateToken(createdUser.getEmail(), createdUser.getRole());

        return Map.of(
                "user", createdUser,
                "token", token,
                "message", "User registered successfully"
        );
    }
}
