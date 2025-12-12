package com.example.keycloakdemo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy; // Import for SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/index.html", "/register/**").permitAll() // Разрешить доступ к странице регистрации и ее подпутям
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/").permitAll() // Разрешить доступ к статическим ресурсам и корневому пути
                    .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
            )
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .userInfoEndpoint(userInfo ->
                        userInfo.oidcUserService(this.oidcUserService())
                    )
                    .defaultSuccessUrl("/") // Redirect to home page after successful login
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
                
                // Extract roles from realm_access.roles claim
                @SuppressWarnings("unchecked")
                List<String> realmRoles = (List<String>) oidcUser.getIdToken().getClaimAsMap("realm_access").get("roles");
                log.info("realm.roles = {}", realmRoles.toString());

                // Add existing authorities from the delegate's OidcUser (scopes like OIDC_USER, SCOPE_email, etc.)
                Set<GrantedAuthority> combinedAuthorities = new HashSet<>(oidcUser.getAuthorities());
    
                // Extract and add role-based authorities
                if (realmRoles != null) {
                    realmRoles.forEach(role -> combinedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
                }
    
                // Return a new OidcUser with the combined authorities
                return new DefaultOidcUser(combinedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles"); // Keycloak realm roles claim
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
