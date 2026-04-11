package com.example.car.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "cars")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Car {

    @Id
    private String id;

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

    @CreatedDate
    private LocalDateTime createdAt;
}
