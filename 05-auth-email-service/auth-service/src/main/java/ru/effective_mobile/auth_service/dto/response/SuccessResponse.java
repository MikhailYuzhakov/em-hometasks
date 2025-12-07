package ru.effective_mobile.auth_service.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SuccessResponse {
    private final String message;
    private final LocalDateTime timestamp;

    public SuccessResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
