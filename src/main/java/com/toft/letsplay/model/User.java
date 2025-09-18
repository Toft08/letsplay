package com.toft.letsplay.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String Id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @Email
    @NotBlank(message = "Email can't be empty")
    private String email;

    @NotBlank(message = "Password can't be empty")
    private String password;

    @NotBlank(message = "Please select a role")
    private String role;

    // getters and setters
}