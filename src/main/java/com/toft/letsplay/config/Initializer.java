package com.toft.letsplay.config;

import com.toft.letsplay.model.User;
import com.toft.letsplay.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class Initializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        };
    }
}
