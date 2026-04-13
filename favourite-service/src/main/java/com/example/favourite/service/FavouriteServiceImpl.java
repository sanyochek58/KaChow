package com.example.favourite.service;

import com.example.favourite.dto.response.FavouriteResponse;
import com.example.favourite.exception.FavouriteException;
import com.example.favourite.model.Favourite;
import com.example.favourite.repository.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteRepository favouriteRepository;

    @Override
    public FavouriteResponse add(String userId, String carId, String dealerId) {
        if (favouriteRepository.existsByUserIdAndCarId(userId, carId)) {
            throw new FavouriteException("Автомобиль уже в избранном");
        }

        Favourite favourite = Favourite.builder()
                .userId(userId)
                .carId(carId)
                .dealerId(dealerId)
                .build();

        favouriteRepository.save(favourite);
        log.info("Пользователь {} добавил авто {} в избранное", userId, carId);

        return buildResponse(favourite);
    }

    @Override
    public void remove(String userId, String carId) {
        if (!favouriteRepository.existsByUserIdAndCarId(userId, carId)) {
            throw new FavouriteException("Автомобиль не найден в избранном");
        }

        favouriteRepository.deleteByUserIdAndCarId(userId, carId);
        log.info("Пользователь {} удалил авто {} из избранного", userId, carId);
    }

    @Override
    public List<FavouriteResponse> findByUserId(String userId) {
        return favouriteRepository.findByUserId(userId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public boolean isFavourite(String userId, String carId) {
        return favouriteRepository.existsByUserIdAndCarId(userId, carId);
    }

    private FavouriteResponse buildResponse(Favourite favourite) {
        return FavouriteResponse.builder()
                .id(favourite.getId())
                .userId(favourite.getUserId())
                .carId(favourite.getCarId())
                .dealerId(favourite.getDealerId())
                .addedAt(favourite.getAddedAt())
                .build();
    }
}