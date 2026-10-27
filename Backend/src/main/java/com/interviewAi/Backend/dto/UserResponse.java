package com.interviewAi.Backend.dto;

public class UserResponse {

    private Long id;
    private String name;
    private String email;

    public UserResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Response mein user ki public details return karta hai
    public Long getId() {
        return id; }

    public String getName() {
        return name; }

    public String getEmail() {
        return email; }
}
