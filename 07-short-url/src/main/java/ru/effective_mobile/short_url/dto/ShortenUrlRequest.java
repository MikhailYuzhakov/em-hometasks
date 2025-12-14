package ru.effective_mobile.short_url.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import lombok.Getter;
import lombok.Setter;
import ru.effective_mobile.short_url.validation.FutureOrPresentDate;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ShortenUrlRequest {

    @NotBlank(message = "Original URL cannot be empty")
    @URL(message = "Original URL must be a valid URL")
    private String originalUrl;

    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,255}$", message = "Alias must be alphanumeric, contain hyphens or underscores, and be between 3 and 255 characters long")
    private String alias;

    @FutureOrPresentDate
    private OffsetDateTime expiresAt;
}
