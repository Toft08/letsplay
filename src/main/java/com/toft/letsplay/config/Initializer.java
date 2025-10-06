package com.toft.letsplay.config;

import com.toft.letsplay.model.Product;
import com.toft.letsplay.model.User;
import com.toft.letsplay.repository.ProductRepository;
import com.toft.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Initializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, ProductRepository productRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Users
            if (userRepository.findByEmail("admin@ex.ax").isEmpty()) {
                userRepository.save(new User(
                        null,
                        "Admin",
                        "admin@ex.ax",
                        encoder.encode("123!"),
                        "ADMIN"
                ));
            }
            if (userRepository.findByEmail("user1@ex.ax").isEmpty()) {
                userRepository.save(new User(
                        null,
                        "Uo",
                        "user1@ex.ax",
                        encoder.encode("123!"),
                        "USER"
                ));
            }
            if (userRepository.findByEmail("user2@ex.ax").isEmpty()) {
                userRepository.save(new User(
                        null,
                        "Ut",
                        "user2@ex.ax",
                        encoder.encode("123!"),
                        "USER"
                ));
            }

            // Products
            User user1 = userRepository.findByEmail("user1@ex.ax").orElse(null);
            User user2 = userRepository.findByEmail("user2@ex.ax").orElse(null);

            if (user1 != null && productRepository.findByUserId(user1.getId()).isEmpty()) {
                productRepository.save(new Product(
                        null,
                        "Mako3",
                        "A straight flying disc",
                        15.0,
                        user1.getId()
                ));
                productRepository.save(new Product(
                        null,
                        "Basket",
                        "Standard disc golf basket",
                        99.99,
                        user1.getId()
                ));
            }
            if (user2 != null && productRepository.findByUserId(user2.getId()).isEmpty()) {
                productRepository.save(new Product(
                        null,
                        "Darts",
                        "New set of darts from Mission",
                        60.50,
                        user2.getId()
                ));
            }
        };
    }
}
