package com.example.favourite.controller;

import com.example.favourite.dto.response.FavouriteResponse;
import com.example.favourite.service.FavouriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
@Tag(name = "Избранное", description = "Управление избранными автомобилями")
public class FavouriteController {

    private final FavouriteService favouriteService;

    @Operation(summary = "Добавить в избранное")
    @PostMapping("/{carId}")
    public ResponseEntity<FavouriteResponse> add(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String carId,
            @RequestParam String dealerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favouriteService.add(userId, carId, dealerId));
    }

    @Operation(summary = "Удалить из избранного")
    @DeleteMapping("/{carId}")
    public ResponseEntity<Map<String, String>> remove(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String carId) {
        favouriteService.remove(userId, carId);
        return ResponseEntity.ok(Map.of("message", "Удалено из избранного"));
    }

    @Operation(summary = "Получить избранное пользователя")
    @GetMapping
    public ResponseEntity<List<FavouriteResponse>> getAll(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(favouriteService.findByUserId(userId));
    }

    @Operation(summary = "Проверить — в избранном ли авто")
    @GetMapping("/{carId}/check")
    public ResponseEntity<Map<String, Boolean>> check(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String carId) {
        return ResponseEntity.ok(Map.of("isFavourite", favouriteService.isFavourite(userId, carId)));
    }
}