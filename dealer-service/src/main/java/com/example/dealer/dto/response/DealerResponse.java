package com.example.dealer.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerResponse {

    private String id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private String description;
    private LocalDateTime createdAt;
}
