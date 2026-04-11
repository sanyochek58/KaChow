package com.example.car.service;

import com.example.car.dto.request.CarRequest;
import com.example.car.dto.response.CarResponse;
import com.example.car.exception.CarException;
import com.example.car.kafka.event.CarPriceChangedEvent;
import com.example.car.kafka.event.StatusCarChangedEvent;
import com.example.car.kafka.producer.CarEventProducer;
import com.example.car.model.Car;
import com.example.car.model.StatusCar;
import com.example.car.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarEventProducer carEventProducer;

    @Override
    public CarResponse create(CarRequest request) {
        Car car = Car.builder()
                .dealerId(request.getDealerId())
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .engineVolume(request.getEngineVolume())
                .mileage(request.getMileage())
                .price(request.getPrice())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();

        carRepository.save(car);
        log.info("Добавлен новый автомобиль: {} {} в салон {}",
                car.getBrand(), car.getModel(), car.getDealerId());

        return buildCarResponse(car);
    }

    @Override
    public CarResponse update(String id, CarRequest request) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new CarException("Автомобиль не найден"));

        if (!car.getPrice().equals(request.getPrice())) {
            carEventProducer.sendPriceChangeEvent(CarPriceChangedEvent.builder()
                    .carId(car.getId())
                    .dealerId(car.getDealerId())
                    .brand(car.getBrand())
                    .model(car.getModel())
                    .oldPrice(car.getPrice())
                    .newPrice(request.getPrice())
                    .build());
        }

        if (!car.getStatus().equals(request.getStatus())) {
            carEventProducer.sendStatusChangeEvent(StatusCarChangedEvent.builder()
                    .carId(car.getId())
                    .dealerId(car.getDealerId())
                    .brand(car.getBrand())
                    .model(car.getModel())
                    .oldStatus(car.getStatus())
                    .newStatus(request.getStatus())
                    .build());
        }


        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setColor(request.getColor());
        car.setEngineVolume(request.getEngineVolume());
        car.setMileage(request.getMileage());
        car.setPrice(request.getPrice());
        car.setStatus(request.getStatus());
        car.setDescription(request.getDescription());

        carRepository.save(car);
        log.info("Автомобиль обновлён: {}", car.getId());

        return buildCarResponse(car);
    }

    @Override
    public void delete(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarException("Автомобиль не найден"));
        carRepository.deleteById(id);
        log.info("Автомобиль удалён: {}", car.getId());
    }

    @Override
    public CarResponse findById(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarException("Автомобиль не найден"));
        return buildCarResponse(car);
    }

    @Override
    public List<CarResponse> findByDealerId(String dealerId) {
        return carRepository.findByDealerId(dealerId)
                .stream()
                .map(this::buildCarResponse)
                .toList();
    }

    @Override
    public List<CarResponse> findByDealerIdAndStatus(String dealerId, StatusCar status) {
        return carRepository.findByDealerIdAndStatus(dealerId, status)
                .stream()
                .map(this::buildCarResponse)
                .toList();
    }

    @Override
    public List<CarResponse> findByDealerIdAndBrand(String dealerId, String brand) {
        return carRepository.findByDealerIdAndBrandIgnoreCase(dealerId, brand)
                .stream()
                .map(this::buildCarResponse)
                .toList();
    }

    @Override
    public List<CarResponse> findByDealerIdAndPriceRange(String dealerId, Double priceMin, Double priceMax) {
        return carRepository.findByDealerIdAndPriceBetween(dealerId, priceMin, priceMax)
                .stream()
                .map(this::buildCarResponse)
                .toList();
    }

    private CarResponse buildCarResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .dealerId(car.getDealerId())
                .brand(car.getBrand())
                .model(car.getModel())
                .year(car.getYear())
                .color(car.getColor())
                .engineVolume(car.getEngineVolume())
                .mileage(car.getMileage())
                .price(car.getPrice())
                .status(car.getStatus())
                .description(car.getDescription())
                .createdAt(car.getCreatedAt())
                .build();
    }

}
