package com.example.favourite.repository;

import com.example.favourite.model.Favourite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository extends MongoRepository<Favourite,String> {
    List<Favourite> findByUserId(String userId);

    Optional<Favourite> findByUserIdAndCarId(String userId, String carId);

    boolean existsByUserIdAndCarId(String userId, String carId);

    void deleteByUserIdAndCarId(String userId, String carId);

    List<Favourite> findByDealerId(String dealerId);
}
