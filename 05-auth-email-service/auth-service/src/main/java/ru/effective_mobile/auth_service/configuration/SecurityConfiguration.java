package ru.effective_mobile.auth_service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.effective_mobile.auth_service.exceptions.GlobalSecurityExceptionHandler;
import ru.effective_mobile.auth_service.security.CustomAuthProvider;
import ru.effective_mobile.auth_service.utils.JwtAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final CustomAuthProvider provider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final GlobalSecurityExceptionHandler securityExceptionHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF, так как у нас REST API
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/sign-in", "/auth/sign-up").permitAll() // Разрешаем доступ к /auth/register и /auth/login
                        .anyRequest().authenticated())

                .exceptionHandling(configure -> configure
                        .authenticationEntryPoint(securityExceptionHandler) // Для 401
                        .accessDeniedHandler(securityExceptionHandler))     // Для 403

                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS)) // Сессии не храним (JWT)
                .authenticationProvider(provider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager getAuthManager() {
        return provider::authenticate;
    }
}
