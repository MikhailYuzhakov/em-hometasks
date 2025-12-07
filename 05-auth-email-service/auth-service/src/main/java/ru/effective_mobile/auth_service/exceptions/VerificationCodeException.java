package ru.effective_mobile.auth_service.exceptions;

public class VerificationCodeException extends RuntimeException {
    public VerificationCodeException(String message) {
        super(message);
    }
}
