package com.example.car.repository;

import com.example.car.model.Car;
import com.example.car.model.StatusCar;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CarRepository extends MongoRepository<Car, String> {

    List<Car> findByDealerId(String dealerId);

    List<Car> findByDealerIdAndStatus(String dealerId, StatusCar status);

    List<Car> findByDealerIdAndBrandIgnoreCase(String dealerId, String brand);

    List<Car> findByDealerIdAndBrandIgnoreCaseAndModelIgnoreCase(String dealerId, String brand, String model);

    List<Car> findByDealerIdAndPriceBetween(String dealerId, Double priceMin, Double priceMax);

    boolean existsByDealerIdAndBrandAndModel(String dealerId, String brand, String model);
}