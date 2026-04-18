package com.example.favourite.controller;

import com.example.favourite.dto.response.FavouriteResponse;
import com.example.favourite.exception.FavouriteException;
import com.example.favourite.exception.GlobalExceptionHandler;
import com.example.favourite.service.FavouriteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavouriteController.class)
@Import(GlobalExceptionHandler.class)
public class FavouriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavouriteService favouriteService;

    private FavouriteResponse buildResponse() {
        return FavouriteResponse.builder()
                .id("fav-id-1")
                .userId("user-id-1")
                .carId("car-id-1")
                .dealerId("dealer-id-1")
                .build();
    }

    /* ADD */

    @Test
    @DisplayName("POST /api/favourites/{carId} - 201 при успехе")
    public void add_success() throws Exception {
        when(favouriteService.add("user-id-1", "car-id-1", "dealer-id-1")).thenReturn(buildResponse());

        mockMvc.perform(post("/api/favourites/car-id-1")
                        .header("X-User-Id", "user-id-1")
                        .param("dealerId", "dealer-id-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("fav-id-1"))
                .andExpect(jsonPath("$.carId").value("car-id-1"))
                .andExpect(jsonPath("$.userId").value("user-id-1"));
    }

    @Test
    @DisplayName("POST /api/favourites/{carId} - 400 уже в избранном")
    public void add_alreadyExists() throws Exception {
        when(favouriteService.add("user-id-1", "car-id-1", "dealer-id-1"))
                .thenThrow(new FavouriteException("Автомобиль уже в избранном"));

        mockMvc.perform(post("/api/favourites/car-id-1")
                        .header("X-User-Id", "user-id-1")
                        .param("dealerId", "dealer-id-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автомобиль уже в избранном"));
    }

    /* REMOVE */

    @Test
    @DisplayName("DELETE /api/favourites/{carId} - 200 при успехе")
    public void remove_success() throws Exception {
        doNothing().when(favouriteService).remove("user-id-1", "car-id-1");

        mockMvc.perform(delete("/api/favourites/car-id-1")
                        .header("X-User-Id", "user-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Удалено из избранного"));
    }

    @Test
    @DisplayName("DELETE /api/favourites/{carId} - 400 не найден в избранном")
    public void remove_notFound() throws Exception {
        doThrow(new FavouriteException("Автомобиль не найден в избранном"))
                .when(favouriteService).remove("user-id-1", "car-id-1");

        mockMvc.perform(delete("/api/favourites/car-id-1")
                        .header("X-User-Id", "user-id-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Автомобиль не найден в избранном"));
    }

    /* GET ALL */

    @Test
    @DisplayName("GET /api/favourites - 200 список избранного")
    public void getAll_success() throws Exception {
        when(favouriteService.findByUserId("user-id-1"))
                .thenReturn(List.of(buildResponse(), buildResponse()));

        mockMvc.perform(get("/api/favourites")
                        .header("X-User-Id", "user-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/favourites - 200 пустой список")
    public void getAll_empty() throws Exception {
        when(favouriteService.findByUserId("user-id-1")).thenReturn(List.of());

        mockMvc.perform(get("/api/favourites")
                        .header("X-User-Id", "user-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /* CHECK */

    @Test
    @DisplayName("GET /api/favourites/{carId}/check - true")
    public void check_isFavourite() throws Exception {
        when(favouriteService.isFavourite("user-id-1", "car-id-1")).thenReturn(true);

        mockMvc.perform(get("/api/favourites/car-id-1/check")
                        .header("X-User-Id", "user-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFavourite").value(true));
    }

    @Test
    @DisplayName("GET /api/favourites/{carId}/check - false")
    public void check_notFavourite() throws Exception {
        when(favouriteService.isFavourite("user-id-1", "car-id-1")).thenReturn(false);

        mockMvc.perform(get("/api/favourites/car-id-1/check")
                        .header("X-User-Id", "user-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFavourite").value(false));
    }
}
