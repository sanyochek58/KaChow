package com.example.car.service;

import com.example.car.dto.request.CarRequest;
import com.example.car.dto.response.CarResponse;
import com.example.car.exception.CarException;
import com.example.car.kafka.producer.CarEventProducer;
import com.example.car.model.Car;
import com.example.car.model.StatusCar;
import com.example.car.repository.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarEventProducer carEventProducer;

    @InjectMocks
    private CarServiceImpl carService;

    private CarRequest buildRequest() {
        CarRequest request = new CarRequest();
        request.setDealerId("dealer-id-1");
        request.setBrand("Toyota");
        request.setModel("Camry");
        request.setYear(2022);
        request.setColor("Белый");
        request.setEngineVolume(2.5);
        request.setMileage(10000);
        request.setPrice(2500000.0);
        request.setStatus(StatusCar.AVAILABLE);
        request.setDescription("Отличное состояние");
        return request;
    }

    private Car buildCar() {
        return Car.builder()
                .id("car-id-1")
                .dealerId("dealer-id-1")
                .brand("Toyota")
                .model("Camry")
                .year(2022)
                .color("Белый")
                .engineVolume(2.5)
                .mileage(10000)
                .price(2500000.0)
                .status(StatusCar.AVAILABLE)
                .description("Отличное состояние")
                .build();
    }

    /* CREATE */

    @Test
    @DisplayName("create - успешное создание автомобиля")
    public void create_success() {
        when(carRepository.save(any(Car.class))).thenReturn(buildCar());

        CarResponse response = carService.create(buildRequest());

        assertThat(response.getBrand()).isEqualTo("Toyota");
        assertThat(response.getModel()).isEqualTo("Camry");
        assertThat(response.getStatus()).isEqualTo(StatusCar.AVAILABLE);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    /* FIND BY ID */

    @Test
    @DisplayName("findById - найден")
    public void findById_success() {
        when(carRepository.findById("car-id-1")).thenReturn(Optional.of(buildCar()));

        CarResponse response = carService.findById("car-id-1");

        assertThat(response.getId()).isEqualTo("car-id-1");
        assertThat(response.getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("findById - не найден")
    public void findById_notFound() {
        when(carRepository.findById("unknown-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.findById("unknown-id"))
                .isInstanceOf(CarException.class)
                .hasMessage("Автомобиль не найден");
    }

    /* FIND BY DEALER ID */

    @Test
    @DisplayName("findByDealerId - возвращает список")
    public void findByDealerId_success() {
        when(carRepository.findByDealerId("dealer-id-1")).thenReturn(List.of(buildCar(), buildCar()));

        List<CarResponse> result = carService.findByDealerId("dealer-id-1");

        assertThat(result).hasSize(2);
    }

    /* FIND BY STATUS */

    @Test
    @DisplayName("findByDealerIdAndStatus - фильтр по статусу")
    public void findByStatus_success() {
        when(carRepository.findByDealerIdAndStatus("dealer-id-1", StatusCar.AVAILABLE))
                .thenReturn(List.of(buildCar()));

        List<CarResponse> result = carService.findByDealerIdAndStatus("dealer-id-1", StatusCar.AVAILABLE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StatusCar.AVAILABLE);
    }

    /* FIND BY BRAND */

    @Test
    @DisplayName("findByDealerIdAndBrand - фильтр по марке")
    public void findByBrand_success() {
        when(carRepository.findByDealerIdAndBrandIgnoreCase("dealer-id-1", "Toyota"))
                .thenReturn(List.of(buildCar()));

        List<CarResponse> result = carService.findByDealerIdAndBrand("dealer-id-1", "Toyota");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Toyota");
    }

    /* FIND BY PRICE RANGE */

    @Test
    @DisplayName("findByDealerIdAndPriceRange - фильтр по цене")
    public void findByPriceRange_success() {
        when(carRepository.findByDealerIdAndPriceBetween("dealer-id-1", 1000000.0, 3000000.0))
                .thenReturn(List.of(buildCar()));

        List<CarResponse> result = carService.findByDealerIdAndPriceRange("dealer-id-1", 1000000.0, 3000000.0);

        assertThat(result).hasSize(1);
    }

    /* UPDATE */

    @Test
    @DisplayName("update - без изменения цены и статуса")
    public void update_noPriceOrStatusChange() {
        Car existingCar = buildCar();
        when(carRepository.findById("car-id-1")).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(existingCar);

        CarResponse response = carService.update("car-id-1", buildRequest());

        assertThat(response.getBrand()).isEqualTo("Toyota");
        verify(carEventProducer, never()).sendPriceChangeEvent(any());
        verify(carEventProducer, never()).sendStatusChangeEvent(any());
    }

    @Test
    @DisplayName("update - отправляет событие изменения цены")
    public void update_priceChanged() {
        Car existingCar = buildCar();
        when(carRepository.findById("car-id-1")).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(existingCar);

        CarRequest request = buildRequest();
        request.setPrice(3000000.0);

        carService.update("car-id-1", request);

        verify(carEventProducer, times(1)).sendPriceChangeEvent(any());
        verify(carEventProducer, never()).sendStatusChangeEvent(any());
    }

    @Test
    @DisplayName("update - отправляет событие изменения статуса")
    public void update_statusChanged() {
        Car existingCar = buildCar();
        when(carRepository.findById("car-id-1")).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(existingCar);

        CarRequest request = buildRequest();
        request.setStatus(StatusCar.SOLD);

        carService.update("car-id-1", request);

        verify(carEventProducer, times(1)).sendStatusChangeEvent(any());
        verify(carEventProducer, never()).sendPriceChangeEvent(any());
    }

    @Test
    @DisplayName("update - не найден")
    public void update_notFound() {
        when(carRepository.findById("unknown-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.update("unknown-id", buildRequest()))
                .isInstanceOf(CarException.class)
                .hasMessage("Автомобиль не найден");
    }

    /* DELETE */

    @Test
    @DisplayName("delete - успешное удаление")
    public void delete_success() {
        when(carRepository.findById("car-id-1")).thenReturn(Optional.of(buildCar()));
        doNothing().when(carRepository).deleteById("car-id-1");

        carService.delete("car-id-1");

        verify(carRepository, times(1)).deleteById("car-id-1");
    }

    @Test
    @DisplayName("delete - не найден")
    public void delete_notFound() {
        when(carRepository.findById("unknown-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.delete("unknown-id"))
                .isInstanceOf(CarException.class)
                .hasMessage("Автомобиль не найден");
    }
}
