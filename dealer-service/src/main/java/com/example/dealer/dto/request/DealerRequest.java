package com.example.dealer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DealerRequest {

    @NotBlank(message = "Название обязательно")
    private String name;

    @NotBlank(message = "Город обязателен")
    private String city;

    @NotBlank(message = "Адрес обязателен")
    private String address;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный формат телефона")
    private String phone;

    private String description;
}
