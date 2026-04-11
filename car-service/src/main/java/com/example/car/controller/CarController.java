package com.example.car.controller;

import com.example.car.dto.request.CarRequest;
import com.example.car.dto.response.CarResponse;
import com.example.car.model.StatusCar;
import com.example.car.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Tag(name = "Автомобили", description = "CRUD автомобилей и фильтрация")
public class CarController {

    private final CarService carService;

    @Operation(summary = "Добавить автомобиль")
    @PostMapping("/create")
    public ResponseEntity<CarResponse> create(@Valid @RequestBody CarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(request));
    }

    @Operation(summary = "Получить все автомобили автосалона")
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<List<CarResponse>> getByDealerId(@PathVariable String dealerId) {
        return ResponseEntity.ok(carService.findByDealerId(dealerId));
    }

    @Operation(summary = "Получить автомобили по статусу")
    @GetMapping("/dealer/{dealerId}/status/{status}")
    public ResponseEntity<List<CarResponse>> getByStatus(
            @PathVariable String dealerId,
            @PathVariable StatusCar status) {
        return ResponseEntity.ok(carService.findByDealerIdAndStatus(dealerId, status));
    }

    @Operation(summary = "Получить автомобили по марке")
    @GetMapping("/dealer/{dealerId}/brand/{brand}")
    public ResponseEntity<List<CarResponse>> getByBrand(
            @PathVariable String dealerId,
            @PathVariable String brand) {
        return ResponseEntity.ok(carService.findByDealerIdAndBrand(dealerId, brand));
    }

    @Operation(summary = "Получить автомобили по диапазону цен")
    @GetMapping("/dealer/{dealerId}/price")
    public ResponseEntity<List<CarResponse>> getByPriceRange(
            @PathVariable String dealerId,
            @RequestParam Double priceMin,
            @RequestParam Double priceMax) {
        return ResponseEntity.ok(carService.findByDealerIdAndPriceRange(dealerId, priceMin, priceMax));
    }

    @Operation(summary = "Получить автомобиль по id")
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(carService.findById(id));
    }

    @Operation(summary = "Обновить автомобиль")
    @PutMapping("/edit/{id}")
    public ResponseEntity<CarResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CarRequest request) {
        return ResponseEntity.ok(carService.update(id, request));
    }

    @Operation(summary = "Удалить автомобиль")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        carService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Автомобиль удалён"));
    }
}