package com.example.dealer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "dealers")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dealer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String city;

    private String address;

    private String phone;

    private String description;

    @CreatedDate
    private LocalDateTime createdAt;

}
