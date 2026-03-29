package com.example.dealer.controller;

import com.example.dealer.dto.request.DealerRequest;
import com.example.dealer.dto.response.DealerResponse;
import com.example.dealer.service.DealerService;
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
@RequestMapping("/api/dealer")
@RequiredArgsConstructor
@Tag(name = "Сервис автосалонов", description = "CRUD автосалонов и фильтрация по городам")
public class DealerController {

    private final DealerService dealerService;

    @Operation(summary = "Создать автосалон")
    @PostMapping("/create")
    public ResponseEntity<DealerResponse> create(@Valid @RequestBody DealerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealerService.create(request));
    }

    @Operation(summary = "Получить все автосалоны")
    @GetMapping("/dealerships")
    public ResponseEntity<List<DealerResponse>> getAll() {
        return ResponseEntity.ok(dealerService.findAll());
    }

    @Operation(summary = "Получить автосалоны по городу")
    @GetMapping("/dealerships/city/{city}")
    public ResponseEntity<List<DealerResponse>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(dealerService.findByCity(city));
    }

    @Operation(summary = "Получить автосалон по id")
    @GetMapping("/dealerships/{id}")
    public ResponseEntity<DealerResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(dealerService.findById(id));
    }

    @Operation(summary = "Обновить автосалон")
    @PutMapping("/edit/{id}")
    public ResponseEntity<DealerResponse> update(
            @PathVariable String id,
            @Valid @RequestBody DealerRequest request) {
        return ResponseEntity.ok(dealerService.update(id, request));
    }

    @Operation(summary = "Удалить автосалон")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        dealerService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Автосалон удалён"));
    }

}
