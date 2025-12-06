package com.example.keycloakdemo.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
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

    public String createUser(String username, String password, String email, String firstName, String lastName, List<String> groupNames) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setGroups(groupNames);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);

        String userId = getCreatedId(response);
        
        // Assign groups to the user
        if (groupNames != null && !groupNames.isEmpty()) {
            groupNames.forEach(groupName -> {
                Optional<GroupRepresentation> group = findGroupByName(groupName);
                group.ifPresent(groupRepresentation -> 
                    keycloak.realm(realm).users().get(userId).joinGroup(groupRepresentation.getId()));
            });
        }

        return userId;
    }

    public Optional<GroupRepresentation> findGroupByName(String groupName) {
        return keycloak.realm(realm).groups().groups().stream()
                .filter(group -> group.getName().equals(groupName))
                .findFirst();
    }
    
    public List<GroupRepresentation> getAllGroups() {
        return keycloak.realm(realm).groups().groups();
    }

}
