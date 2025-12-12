package com.example.keycloakdemo;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
@NoArgsConstructor
public class RegistrationForm {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private List<String> roles;

    public String getEmail() {
        if (firstName != null && lastName != null) {
            return (firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com");
        }
        return null; 
    }
}
