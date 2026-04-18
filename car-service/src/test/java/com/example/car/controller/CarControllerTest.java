package com.example.car.controller;

import com.example.car.dto.request.CarRequest;
import com.example.car.dto.response.CarResponse;
import com.example.car.exception.CarException;
import com.example.car.exception.GlobalExceptionHandler;
import com.example.car.model.StatusCar;
import com.example.car.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@Import(GlobalExceptionHandler.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarService carService;

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

    private CarResponse buildResponse() {
        return CarResponse.builder()
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
    @DisplayName("POST /api/cars/create - 201 при успехе")
    public void create_success() throws Exception {
        when(carService.create(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/api/cars/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("car-id-1"))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"));
    }

    @Test
    @DisplayName("POST /api/cars/create - 400 при невалидном запросе")
    public void create_invalidRequest() throws Exception {
        CarRequest request = new CarRequest();

        mockMvc.perform(post("/api/cars/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /* GET BY DEALER ID */

    @Test
    @DisplayName("GET /api/cars/dealer/{dealerId} - 200 список авто")
    public void getByDealerId_success() throws Exception {
        when(carService.findByDealerId("dealer-id-1")).thenReturn(List.of(buildResponse(), buildResponse()));

        mockMvc.perform(get("/api/cars/dealer/dealer-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/cars/dealer/{dealerId} - 200 пустой список")
    public void getByDealerId_empty() throws Exception {
        when(carService.findByDealerId("dealer-id-1")).thenReturn(List.of());

        mockMvc.perform(get("/api/cars/dealer/dealer-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /* GET BY STATUS */

    @Test
    @DisplayName("GET /api/cars/dealer/{dealerId}/status/{status} - 200")
    public void getByStatus_success() throws Exception {
        when(carService.findByDealerIdAndStatus("dealer-id-1", StatusCar.AVAILABLE))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/cars/dealer/dealer-id-1/status/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    /* GET BY BRAND */

    @Test
    @DisplayName("GET /api/cars/dealer/{dealerId}/brand/{brand} - 200")
    public void getByBrand_success() throws Exception {
        when(carService.findByDealerIdAndBrand("dealer-id-1", "Toyota"))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/cars/dealer/dealer-id-1/brand/Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Toyota"));
    }

    /* GET BY PRICE RANGE */

    @Test
    @DisplayName("GET /api/cars/dealer/{dealerId}/price - 200")
    public void getByPriceRange_success() throws Exception {
        when(carService.findByDealerIdAndPriceRange("dealer-id-1", 1000000.0, 3000000.0))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/cars/dealer/dealer-id-1/price")
                        .param("priceMin", "1000000.0")
                        .param("priceMax", "3000000.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    /* GET BY ID */

    @Test
    @DisplayName("GET /api/cars/{id} - 200 найден")
    public void getById_success() throws Exception {
        when(carService.findById("car-id-1")).thenReturn(buildResponse());

        mockMvc.perform(get("/api/cars/car-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("car-id-1"))
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    @DisplayName("GET /api/cars/{id} - 400 не найден")
    public void getById_notFound() throws Exception {
        when(carService.findById("unknown-id"))
                .thenThrow(new CarException("Автомобиль не найден"));

        mockMvc.perform(get("/api/cars/unknown-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автомобиль не найден"));
    }

    /* UPDATE */

    @Test
    @DisplayName("PUT /api/cars/edit/{id} - 200 обновлён")
    public void update_success() throws Exception {
        when(carService.update(eq("car-id-1"), any())).thenReturn(buildResponse());

        mockMvc.perform(put("/api/cars/edit/car-id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("car-id-1"));
    }

    @Test
    @DisplayName("PUT /api/cars/edit/{id} - 400 не найден")
    public void update_notFound() throws Exception {
        when(carService.update(eq("unknown-id"), any()))
                .thenThrow(new CarException("Автомобиль не найден"));

        mockMvc.perform(put("/api/cars/edit/unknown-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автомобиль не найден"));
    }

    /* DELETE */

    @Test
    @DisplayName("DELETE /api/cars/delete/{id} - 200 удалён")
    public void delete_success() throws Exception {
        doNothing().when(carService).delete("car-id-1");

        mockMvc.perform(delete("/api/cars/delete/car-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Автомобиль удалён"));
    }

    @Test
    @DisplayName("DELETE /api/cars/delete/{id} - 400 не найден")
    public void delete_notFound() throws Exception {
        doThrow(new CarException("Автомобиль не найден"))
                .when(carService).delete("unknown-id");

        mockMvc.perform(delete("/api/cars/delete/unknown-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автомобиль не найден"));
    }
}
