package com.example.keycloakdemo.controller;

import com.example.keycloakdemo.service.KeycloakAdminService;
import com.example.keycloakdemo.dto.RegistrationForm;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final KeycloakAdminService keycloakAdminService;

    private static final List<String> ALLOWED_GROUPS = Arrays.asList("jira-access", "gitlab-access", "grafana-access");

    public RegistrationController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        // Filter groups to display only allowed ones
        List<GroupRepresentation> allGroups = keycloakAdminService.getAllGroups();
        model.addAttribute("groups", allGroups.stream()
                .map(GroupRepresentation::getName)
                .filter(ALLOWED_GROUPS::contains) // Filter to only show allowed groups
                .collect(Collectors.toList()));
        return "register";
    }

    @PostMapping
    public String processRegistration(@ModelAttribute RegistrationForm registrationForm, Model model) {
        try {
            keycloakAdminService.createUser(
                    registrationForm.getUsername(),
                    registrationForm.getPassword(),
                    registrationForm.getEmail(), // Email is now generated in RegistrationForm
                    registrationForm.getFirstName(),
                    registrationForm.getLastName(),
                    registrationForm.getGroups()
            );
            model.addAttribute("message", "User registered successfully!");
            return "redirect:/register?success";
        } catch (Exception e) {
            model.addAttribute("error", "Error registering user: " + e.getMessage());
            List<GroupRepresentation> allGroups = keycloakAdminService.getAllGroups();
            model.addAttribute("groups", allGroups.stream()
                    .map(GroupRepresentation::getName)
                    .filter(ALLOWED_GROUPS::contains)
                    .collect(Collectors.toList()));
            return "register";
        }
    }
}
