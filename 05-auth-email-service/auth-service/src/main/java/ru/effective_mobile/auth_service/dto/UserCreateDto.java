package ru.effective_mobile.auth_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class UserCreateDto {
    private String email;
    private String password;
    private String code;
}
