package ru.effective_mobile.short_url.exceptions;

public class AliasNotFoundException extends RuntimeException {
    public AliasNotFoundException(String message) {
        super(message);
    }
}
