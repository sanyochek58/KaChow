package com.example.favourite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavouriteResponse {
    private String id;
    private String userId;
    private String carId;
    private String dealerId;
    private LocalDateTime addedAt;
}
