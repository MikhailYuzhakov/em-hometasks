package com.example.keycloakdemo.dto;

import java.util.List;

public class RegistrationForm {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private List<String> groups;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        if (firstName != null && lastName != null) {
            return (firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com");
        }
        return null; // Or throw an exception, depending on desired behavior
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
