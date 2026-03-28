package com.example.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Не корректный email")
    private String email;

    @NotBlank(message = "Пароль обязателен !")
    @Size(min = 6, message = "Пароль минимум 6 символов !")
    private String password;
}
