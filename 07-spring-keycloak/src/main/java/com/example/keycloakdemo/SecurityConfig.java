package com.example.keycloakdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/register/**").permitAll() // Разрешить доступ к странице регистрации и ее подпутям
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll() // Разрешить доступ к статическим ресурсам
                    .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
            )
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .userInfoEndpoint(userInfo ->
                        userInfo.oidcUserService(this.oidcUserService())
                    )
                    .defaultSuccessUrl("/api/jira-access") // Redirect after successful login
            )
            .oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

    @Bean
    public OidcUserService oidcUserService() {
        final OidcUserService delegate = new OidcUserService();
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                OidcUser oidcUser = delegate.loadUser(userRequest);
                
                // Extract groups from UserInfo attributes
                @SuppressWarnings("unchecked")
                List<String> groups = (List<String>) oidcUser.getAttributes().get("groups");
    
                Set<GrantedAuthority> combinedAuthorities = new HashSet<>();
                
                // Add existing authorities from the delegate's OidcUser (scopes like OIDC_USER, SCOPE_email, etc.)
                combinedAuthorities.addAll(oidcUser.getAuthorities());
    
                // Extract and add group-based authorities
                if (groups != null) {
                    groups.forEach(group -> combinedAuthorities.add(new SimpleGrantedAuthority("GROUP_" + group.replace("/", ""))));
                }
    
                // Return a new OidcUser with the combined authorities
                return new DefaultOidcUser(combinedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("groups"); // Keycloak groups claim
        grantedAuthoritiesConverter.setAuthorityPrefix("GROUP_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
