package com.example.keycloakdemo.controller;

import com.example.keycloakdemo.service.KeycloakAdminService;
import com.example.keycloakdemo.dto.RegistrationForm;
import com.example.keycloakdemo.KeycloakServiceException;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final KeycloakAdminService keycloakAdminService;

    @Value("${app.roles.allowed}")
    private List<String> allowedRoles;

    public RegistrationController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        List<RoleRepresentation> allRoles = keycloakAdminService.getAllRoles();
        model.addAttribute("roles", allRoles.stream()
                .map(RoleRepresentation::getName)
                .filter(allowedRoles::contains) // Filter to only show allowed roles
                .collect(Collectors.toList()));
        return "register";
    }

    @PostMapping
    public String processRegistration(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Validation errors occurred.");
            List<RoleRepresentation> allRoles = keycloakAdminService.getAllRoles();
            model.addAttribute("roles", allRoles.stream()
                    .map(RoleRepresentation::getName)
                    .filter(allowedRoles::contains)
                    .collect(Collectors.toList()));
            return "register";
        }

        try {
            keycloakAdminService.createUser(
                    registrationForm.getUsername(),
                    registrationForm.getPassword(),
                    registrationForm.getEmail(), 
                    registrationForm.getFirstName(),
                    registrationForm.getLastName(),
                    registrationForm.getRoles()
            );
            model.addAttribute("message", "User registered successfully!");
            return "redirect:/register?success";
        } catch (KeycloakServiceException e) {
            model.addAttribute("error", "Error registering user: " + e.getMessage());
            List<RoleRepresentation> allRoles = keycloakAdminService.getAllRoles();
            model.addAttribute("roles", allRoles.stream()
                    .map(RoleRepresentation::getName)
                    .filter(allowedRoles::contains)
                    .collect(Collectors.toList()));
            return "register";
        }
    }
}
