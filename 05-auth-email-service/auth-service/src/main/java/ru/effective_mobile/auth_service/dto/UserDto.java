package ru.effective_mobile.auth_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@Setter
public class UserDto extends UserCreateDto {
    private Long id;
    private String role;
    private Boolean isVerified;
}
