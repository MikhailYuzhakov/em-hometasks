package ru.effective_mobile.short_url.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.OffsetDateTime;

public class FutureOrPresentDateValidator implements ConstraintValidator<FutureOrPresentDate, OffsetDateTime> {

    @Override
    public void initialize(FutureOrPresentDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(OffsetDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are considered valid, use @NotNull if needed
        }
        return value.isAfter(OffsetDateTime.now()) || value.isEqual(OffsetDateTime.now());
    }
}
