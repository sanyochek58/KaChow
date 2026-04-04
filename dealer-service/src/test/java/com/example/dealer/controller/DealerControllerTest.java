package com.example.dealer.controller;

import com.example.dealer.dto.request.DealerRequest;
import com.example.dealer.dto.response.DealerResponse;
import com.example.dealer.exception.DealerException;
import com.example.dealer.exception.GlobalExceptionHandler;
import com.example.dealer.service.DealerService;
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

@WebMvcTest(DealerController.class)
@Import(GlobalExceptionHandler.class)
public class DealerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DealerService dealerService;

    private DealerRequest buildRequest() {
        DealerRequest request = new DealerRequest();
        request.setName("Автомир");
        request.setCity("Москва");
        request.setAddress("ул. Ленина, 1");
        request.setPhone("+79991234567");
        request.setDescription("Описание");
        return request;
    }

    private DealerResponse buildResponse() {
        return DealerResponse.builder()
                .id("dealer-id-1")
                .name("Автомир")
                .city("Москва")
                .address("ул. Ленина, 1")
                .phone("+79991234567")
                .description("Описание")
                .build();
    }

    /* CREATE */

    @Test
    @DisplayName("POST /api/dealer/create - 201 при успехе")
    public void createDealer_success() throws Exception {
        when(dealerService.create(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/api/dealer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("dealer-id-1"))
                .andExpect(jsonPath("$.name").value("Автомир"))
                .andExpect(jsonPath("$.city").value("Москва"));
    }

    @Test
    @DisplayName("POST /api/dealer/create - 400 название занято")
    public void createDealer_nameExists() throws Exception {
        when(dealerService.create(any()))
                .thenThrow(new DealerException("Автосалон с таким именем уже существует"));

        mockMvc.perform(post("/api/dealer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автосалон с таким именем уже существует"));
    }

    @Test
    @DisplayName("POST /api/dealer/create - 400 пустое название")
    public void createDealer_emptyName() throws Exception {
        DealerRequest request = buildRequest();
        request.setName("");

        mockMvc.perform(post("/api/dealer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/dealer/create - 400 пустой город")
    public void createDealer_emptyCity() throws Exception {
        DealerRequest request = buildRequest();
        request.setCity("");

        mockMvc.perform(post("/api/dealer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/dealer/create - 400 пустой адрес")
    public void createDealer_emptyAddress() throws Exception {
        DealerRequest request = buildRequest();
        request.setAddress("");

        mockMvc.perform(post("/api/dealer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/dealer/create - 400 некорректный телефон")
    public void createDealer_invalidPhone() throws Exception {
        DealerRequest request = buildRequest();
        request.setPhone("abc");

        mockMvc.perform(post("/api/dealer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /* GET ALL */

    @Test
    @DisplayName("GET /api/dealer/dealerships - 200 список автосалонов")
    public void getAll_success() throws Exception {
        when(dealerService.findAll()).thenReturn(List.of(buildResponse(), buildResponse()));

        mockMvc.perform(get("/api/dealer/dealerships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/dealer/dealerships - 200 пустой список")
    public void getAll_empty() throws Exception {
        when(dealerService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/dealer/dealerships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /* GET BY CITY */

    @Test
    @DisplayName("GET /api/dealer/dealerships/city/{city} - 200 по городу")
    public void getByCity_success() throws Exception {
        when(dealerService.findByCity("Москва")).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/dealer/dealerships/city/Москва"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].city").value("Москва"));
    }

    /* GET BY ID */

    @Test
    @DisplayName("GET /api/dealer/dealerships/{id} - 200 найден")
    public void getById_success() throws Exception {
        when(dealerService.findById("dealer-id-1")).thenReturn(buildResponse());

        mockMvc.perform(get("/api/dealer/dealerships/dealer-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("dealer-id-1"))
                .andExpect(jsonPath("$.name").value("Автомир"));
    }

    @Test
    @DisplayName("GET /api/dealer/dealerships/{id} - 400 не найден")
    public void getById_notFound() throws Exception {
        when(dealerService.findById("unknown-id"))
                .thenThrow(new DealerException("Автосалон не найден"));

        mockMvc.perform(get("/api/dealer/dealerships/unknown-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автосалон не найден"));
    }

    /* UPDATE */

    @Test
    @DisplayName("PUT /api/dealer/edit/{id} - 200 обновлён")
    public void update_success() throws Exception {
        when(dealerService.update(eq("dealer-id-1"), any())).thenReturn(buildResponse());

        mockMvc.perform(put("/api/dealer/edit/dealer-id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("dealer-id-1"));
    }

    @Test
    @DisplayName("PUT /api/dealer/edit/{id} - 400 не найден")
    public void update_notFound() throws Exception {
        when(dealerService.update(eq("unknown-id"), any()))
                .thenThrow(new DealerException("Автосалон не найден"));

        mockMvc.perform(put("/api/dealer/edit/unknown-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автосалон не найден"));
    }

    /* DELETE */

    @Test
    @DisplayName("DELETE /api/dealer/delete/{id} - 200 удалён")
    public void delete_success() throws Exception {
        doNothing().when(dealerService).delete("dealer-id-1");

        mockMvc.perform(delete("/api/dealer/delete/dealer-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Автосалон удалён"));
    }

    @Test
    @DisplayName("DELETE /api/dealer/delete/{id} - 400 не найден")
    public void delete_notFound() throws Exception {
        doThrow(new DealerException("Автосалон не найден"))
                .when(dealerService).delete("unknown-id");

        mockMvc.perform(delete("/api/dealer/delete/unknown-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автосалон не найден"));
    }
}