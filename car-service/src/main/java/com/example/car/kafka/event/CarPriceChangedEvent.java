package com.example.car.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarPriceChangedEvent {
    private String carId;
    private String dealerId;
    private String brand;
    private String model;
    private Double oldPrice;
    private Double newPrice;
}
