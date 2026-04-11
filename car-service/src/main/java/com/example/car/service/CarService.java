package com.example.car.service;

import com.example.car.dto.request.CarRequest;
import com.example.car.dto.response.CarResponse;
import com.example.car.model.StatusCar;

import java.util.List;

public interface CarService {
    CarResponse create(CarRequest request);
    CarResponse update(String id, CarRequest request);
    void delete(String id);
    CarResponse findById(String id);
    List<CarResponse> findByDealerId(String dealerId);
    List<CarResponse> findByDealerIdAndStatus(String dealerId, StatusCar status);
    List<CarResponse> findByDealerIdAndBrand(String dealerId, String brand);
    List<CarResponse> findByDealerIdAndPriceRange(String dealerId, Double priceMin, Double priceMax);
}