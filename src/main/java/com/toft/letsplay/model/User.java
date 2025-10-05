package com.toft.letsplay.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotBlank(message = "Name can't be empty")
    @Size(min = 2, max = 50)
    private String name;

    @Email
    @NotBlank(message = "Email can't be empty")
    private String email;

    @NotBlank(message = "Password can't be empty")
    @Size(min = 3)
    private String password;

    @NotNull
    private String role;

    public User() {}

    public User(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }


    // Probably needs reworking later...
    public String getId() {return id;}
    // MongoDB might generate ID automatically
    // public void setId(Sting id) { this.is = id; }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getEmail() { return email;}
    public void setEmail( String email) { this.email = email;}

    //Should this be returned?
    public String getPassword() { return password; }
    public void setPassword(String password) {this.password =password; }

    public String getRole() { return role; }
    public void setRole(String role) {this.role = role; }
}