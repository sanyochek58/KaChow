package com.example.favourite.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "favourites")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Favourite {

    @Id
    private String id;

    private String userId;

    private String carId;

    private String dealerId;

    @CreatedDate
    private LocalDateTime addedAt;
}
