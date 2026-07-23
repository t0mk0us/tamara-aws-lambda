package com.tamara.aws.model;

public class User {
    private String name;
    private String email;

    // Default constructor for Jackson
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}