package com.example.favourite.service;

import com.example.favourite.dto.response.FavouriteResponse;

import java.util.List;

public interface FavouriteService {
    FavouriteResponse add(String userId, String carId, String dealerId);
    void remove(String userId, String carId);
    List<FavouriteResponse> findByUserId(String userId);
    boolean isFavourite(String userId, String carId);
}