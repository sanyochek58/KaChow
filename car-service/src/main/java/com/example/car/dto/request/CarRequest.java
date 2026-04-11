package com.example.car.dto.request;

import com.example.car.model.StatusCar;
import com.example.car.model.StatusCar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CarRequest {

    @NotBlank(message = "Id автосалона обязателен")
    private String dealerId;

    @NotBlank(message = "Марка обязательна")
    private String brand;

    @NotBlank(message = "Модель обязательна")
    private String model;

    @NotNull(message = "Год обязателен")
    @Positive(message = "Год должен быть положительным числом")
    private Integer year;

    @NotBlank(message = "Цвет обязателен")
    private String color;

    @NotNull(message = "Объём двигателя обязателен")
    @Positive(message = "Объём двигателя должен быть положительным числом")
    private Double engineVolume;

    @NotNull(message = "Пробег обязателен")
    @Positive(message = "Пробег должен быть положительным числом")
    private Integer mileage;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительным числом")
    private Double price;

    @NotNull(message = "Статус обязателен")
    private StatusCar status;

    private String description;
}