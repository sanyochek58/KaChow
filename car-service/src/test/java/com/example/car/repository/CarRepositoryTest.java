package com.example.car.repository;

import com.example.car.model.Car;
import com.example.car.model.StatusCar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
        carRepository.save(Car.builder()
                .dealerId("dealer-1")
                .brand("Toyota")
                .model("Camry")
                .year(2022)
                .color("Белый")
                .engineVolume(2.5)
                .mileage(10000)
                .price(2500000.0)
                .status(StatusCar.AVAILABLE)
                .build());
        carRepository.save(Car.builder()
                .dealerId("dealer-1")
                .brand("BMW")
                .model("X5")
                .year(2023)
                .color("Чёрный")
                .engineVolume(3.0)
                .mileage(5000)
                .price(6000000.0)
                .status(StatusCar.SOLD)
                .build());
        carRepository.save(Car.builder()
                .dealerId("dealer-2")
                .brand("Toyota")
                .model("RAV4")
                .year(2021)
                .color("Серебристый")
                .engineVolume(2.0)
                .mileage(30000)
                .price(2000000.0)
                .status(StatusCar.AVAILABLE)
                .build());
    }

    @AfterEach
    void tearDown() {
        carRepository.deleteAll();
    }

    @Test
    @DisplayName("findByDealerId - возвращает авто салона")
    public void findByDealerId_success() {
        List<Car> cars = carRepository.findByDealerId("dealer-1");
        assertThat(cars).hasSize(2);
        assertThat(cars).allMatch(c -> c.getDealerId().equals("dealer-1"));
    }

    @Test
    @DisplayName("findByDealerIdAndStatus - фильтр по статусу AVAILABLE")
    public void findByDealerIdAndStatus_available() {
        List<Car> cars = carRepository.findByDealerIdAndStatus("dealer-1", StatusCar.AVAILABLE);
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("findByDealerIdAndStatus - фильтр по статусу SOLD")
    public void findByDealerIdAndStatus_sold() {
        List<Car> cars = carRepository.findByDealerIdAndStatus("dealer-1", StatusCar.SOLD);
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getBrand()).isEqualTo("BMW");
    }

    @Test
    @DisplayName("findByDealerIdAndBrandIgnoreCase - регистронезависимый поиск")
    public void findByDealerIdAndBrandIgnoreCase_caseInsensitive() {
        List<Car> cars = carRepository.findByDealerIdAndBrandIgnoreCase("dealer-1", "toyota");
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("findByDealerIdAndBrandIgnoreCase - марка не найдена")
    public void findByDealerIdAndBrandIgnoreCase_notFound() {
        List<Car> cars = carRepository.findByDealerIdAndBrandIgnoreCase("dealer-1", "Honda");
        assertThat(cars).isEmpty();
    }

    @Test
    @DisplayName("findByDealerIdAndPriceBetween - фильтр по диапазону цен")
    public void findByDealerIdAndPriceBetween_success() {
        List<Car> cars = carRepository.findByDealerIdAndPriceBetween("dealer-1", 2000000.0, 3000000.0);
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("findByDealerIdAndPriceBetween - вне диапазона")
    public void findByDealerIdAndPriceBetween_outOfRange() {
        List<Car> cars = carRepository.findByDealerIdAndPriceBetween("dealer-1", 100000.0, 500000.0);
        assertThat(cars).isEmpty();
    }

    @Test
    @DisplayName("existsByDealerIdAndBrandAndModel - существует")
    public void existsByDealerIdAndBrandAndModel_true() {
        boolean exists = carRepository.existsByDealerIdAndBrandAndModel("dealer-1", "Toyota", "Camry");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByDealerIdAndBrandAndModel - не существует")
    public void existsByDealerIdAndBrandAndModel_false() {
        boolean exists = carRepository.existsByDealerIdAndBrandAndModel("dealer-1", "Honda", "Civic");
        assertThat(exists).isFalse();
    }
}
