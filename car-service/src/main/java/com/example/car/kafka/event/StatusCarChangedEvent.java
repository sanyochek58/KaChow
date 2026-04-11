package com.example.car.kafka.event;

import com.example.car.model.StatusCar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusCarChangedEvent {
    private String carId;
    private String dealerId;
    private String brand;
    private String model;
    private StatusCar oldStatus;
    private StatusCar newStatus;
}
