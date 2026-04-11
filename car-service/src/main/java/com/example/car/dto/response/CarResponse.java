package com.example.car.dto.response;

import com.example.car.model.StatusCar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarResponse {
    private String  id;
    private String dealerId;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private Double engineVolume;
    private Integer mileage;
    private Double price;
    private StatusCar status;
    private String description;
    private LocalDateTime createdAt;
}
