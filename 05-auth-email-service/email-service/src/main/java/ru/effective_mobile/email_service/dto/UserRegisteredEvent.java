package ru.effective_mobile.email_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRegisteredEvent {
    private String email;
    private String verificationCode;
}
