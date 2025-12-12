package com.example.keycloakdemo.service;

import com.example.keycloakdemo.KeycloakServiceException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;
    @Value("${keycloak.admin.realm}")
    private String realm;
    @Value("${keycloak.admin.client-id}")
    private String clientId;
    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        initKeycloak();
    }

    private void initKeycloak() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
    }

    public String createUser(String username, String password, String email, String firstName, String lastName, List<String> roleNames) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);

        String userId = getCreatedId(response);
        if (userId == null) {
            throw new KeycloakServiceException("Failed to create user: userId is null");
        }
        
        // Assign roles to the user
        if (roleNames != null && !roleNames.isEmpty()) {
            roleNames.forEach(roleName -> {
                try {
                    RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
                    keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
                } catch (Exception e) {
                    throw new KeycloakServiceException("Failed to assign role " + roleName + " to user " + username, e);
                }
            });
        }

        return userId;
    }

    public Optional<RoleRepresentation> findRoleByName(String roleName) {
        return keycloak.realm(realm).roles().list().stream()
                .filter(role -> role.getName().equals(roleName))
                .findFirst();
    }
    
    public List<RoleRepresentation> getAllRoles() {
        return keycloak.realm(realm).roles().list();
    }
}
