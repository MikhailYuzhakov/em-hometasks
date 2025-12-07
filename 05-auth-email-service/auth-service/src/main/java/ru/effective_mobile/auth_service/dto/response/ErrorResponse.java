package ru.effective_mobile.auth_service.dto.response;

import ru.effective_mobile.auth_service.dto.ErrorType;

public record ErrorResponse(ErrorType code, String message) {}
