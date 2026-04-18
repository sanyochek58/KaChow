package com.example.favourite.repository;

import com.example.favourite.model.Favourite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class FavouriteRepositoryTest {

    @Autowired
    private FavouriteRepository favouriteRepository;

    @BeforeEach
    void setUp() {
        favouriteRepository.deleteAll();
        favouriteRepository.save(Favourite.builder()
                .userId("user-1")
                .carId("car-1")
                .dealerId("dealer-1")
                .build());
        favouriteRepository.save(Favourite.builder()
                .userId("user-1")
                .carId("car-2")
                .dealerId("dealer-1")
                .build());
        favouriteRepository.save(Favourite.builder()
                .userId("user-2")
                .carId("car-1")
                .dealerId("dealer-2")
                .build());
    }

    @AfterEach
    void tearDown() {
        favouriteRepository.deleteAll();
    }

    @Test
    @DisplayName("findByUserId - возвращает избранное пользователя")
    public void findByUserId_success() {
        List<Favourite> favourites = favouriteRepository.findByUserId("user-1");
        assertThat(favourites).hasSize(2);
        assertThat(favourites).allMatch(f -> f.getUserId().equals("user-1"));
    }

    @Test
    @DisplayName("findByUserId - другой пользователь")
    public void findByUserId_otherUser() {
        List<Favourite> favourites = favouriteRepository.findByUserId("user-2");
        assertThat(favourites).hasSize(1);
        assertThat(favourites.get(0).getCarId()).isEqualTo("car-1");
    }

    @Test
    @DisplayName("findByUserIdAndCarId - найден")
    public void findByUserIdAndCarId_found() {
        Optional<Favourite> favourite = favouriteRepository.findByUserIdAndCarId("user-1", "car-1");
        assertThat(favourite).isPresent();
        assertThat(favourite.get().getDealerId()).isEqualTo("dealer-1");
    }

    @Test
    @DisplayName("findByUserIdAndCarId - не найден")
    public void findByUserIdAndCarId_notFound() {
        Optional<Favourite> favourite = favouriteRepository.findByUserIdAndCarId("user-1", "car-99");
        assertThat(favourite).isEmpty();
    }

    @Test
    @DisplayName("existsByUserIdAndCarId - существует")
    public void existsByUserIdAndCarId_true() {
        boolean exists = favouriteRepository.existsByUserIdAndCarId("user-1", "car-1");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserIdAndCarId - не существует")
    public void existsByUserIdAndCarId_false() {
        boolean exists = favouriteRepository.existsByUserIdAndCarId("user-1", "car-99");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("deleteByUserIdAndCarId - удаляет запись")
    public void deleteByUserIdAndCarId_success() {
        favouriteRepository.deleteByUserIdAndCarId("user-1", "car-1");
        Optional<Favourite> favourite = favouriteRepository.findByUserIdAndCarId("user-1", "car-1");
        assertThat(favourite).isEmpty();
    }

    @Test
    @DisplayName("findByDealerId - возвращает избранное по автосалону")
    public void findByDealerId_success() {
        List<Favourite> favourites = favouriteRepository.findByDealerId("dealer-1");
        assertThat(favourites).hasSize(2);
        assertThat(favourites).allMatch(f -> f.getDealerId().equals("dealer-1"));
    }
}
