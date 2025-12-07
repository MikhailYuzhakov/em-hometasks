package ru.effective_mobile.auth_service.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Длина имени пользователя должна быть от 5 до 50 символов")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    private String password;
}
