
package com.example.keycloakdemo.controller;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/jira-access")
    @PreAuthorize("hasAuthority('GROUP_jira-access')")
    public void jiraAccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://jira.effective-mobile.ru");
        
    }

    @GetMapping("/gitlab-access")
    @PreAuthorize("hasAuthority('GROUP_gitlab-access')")
    public void gitlabAccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://gitlab.effective-mobile.ru");
    }

    @GetMapping("/grafana-access")
    @PreAuthorize("hasAuthority('GROUP_grafana-access')")
    public void confluenceAccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://grafana.em-gitlab.ru/");
    }
}
