package com.example.model;

import java.util.UUID;

public class User {
    private String id;
    private String username;
    private String passwordHash;
    private String email;

    public User(String username, String passwordHash, String email) {
        this.id = UUID.randomUUID().toString(); // Using UUID for simplicity
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
               "id='" + id + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
