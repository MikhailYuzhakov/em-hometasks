package com.example.keycloakdemo.controller;

import java.io.IOException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/jira-access")
    @PreAuthorize("hasRole('ADMIN')")
    public void jiraAccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://jira.effective-mobile.ru");
    }

    @GetMapping("/gitlab-access")
    @PreAuthorize("hasRole('USER')")
    public void gitlabAccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://gitlab.effective-mobile.ru");
    }

    @GetMapping("/grafana-access")
    @PreAuthorize("hasRole('MODERATOR')")
    public void grafanaAccess(HttpServletResponse response) throws IOException {
        log.info("grafanaAccess: redirect to https://grafana.em-gitlab.ru/");
        response.sendRedirect("https://grafana.em-gitlab.ru/");
    }

    @GetMapping("/whoami")
    public String whoAmI(Authentication authentication) {
        if (authentication == null) return "Authentication is NULL";

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return "User: " + authentication.getName() + "<br>Roles: " + roles;
    }
}
