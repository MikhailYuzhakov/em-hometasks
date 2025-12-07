package ru.effective_mobile.auth_service.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.effective_mobile.auth_service.dto.response.ErrorResponse;
import ru.effective_mobile.auth_service.dto.ErrorType;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    // Срабатывает при ошибке аутентификации (401)
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.error("Unauthorized error: {}", authException.getMessage());
        handleException(response, "Unauthorized request", 401);
    }

    // Срабатывает при ошибке авторизации (403) - например, нет роли ADMIN
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.error("Access denied error: {}", accessDeniedException.getMessage());
        handleException(response, "Insufficient permissions to access the resource", 403);
    }

    private void handleException(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(ErrorType.AUTH_ERROR, message);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
