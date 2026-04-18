package com.example.favourite.service;

import com.example.favourite.dto.response.FavouriteResponse;
import com.example.favourite.exception.FavouriteException;
import com.example.favourite.model.Favourite;
import com.example.favourite.repository.FavouriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavouriteServiceImplTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private Favourite buildFavourite() {
        return Favourite.builder()
                .id("fav-id-1")
                .userId("user-id-1")
                .carId("car-id-1")
                .dealerId("dealer-id-1")
                .build();
    }

    /* ADD */

    @Test
    @DisplayName("add - успешное добавление в избранное")
    public void add_success() {
        when(favouriteRepository.existsByUserIdAndCarId("user-id-1", "car-id-1")).thenReturn(false);
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(buildFavourite());

        FavouriteResponse response = favouriteService.add("user-id-1", "car-id-1", "dealer-id-1");

        assertThat(response.getUserId()).isEqualTo("user-id-1");
        assertThat(response.getCarId()).isEqualTo("car-id-1");
        assertThat(response.getDealerId()).isEqualTo("dealer-id-1");
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    @DisplayName("add - уже в избранном")
    public void add_alreadyExists() {
        when(favouriteRepository.existsByUserIdAndCarId("user-id-1", "car-id-1")).thenReturn(true);

        assertThatThrownBy(() -> favouriteService.add("user-id-1", "car-id-1", "dealer-id-1"))
                .isInstanceOf(FavouriteException.class)
                .hasMessage("Автомобиль уже в избранном");

        verify(favouriteRepository, never()).save(any());
    }

    /* REMOVE */

    @Test
    @DisplayName("remove - успешное удаление из избранного")
    public void remove_success() {
        when(favouriteRepository.existsByUserIdAndCarId("user-id-1", "car-id-1")).thenReturn(true);
        doNothing().when(favouriteRepository).deleteByUserIdAndCarId("user-id-1", "car-id-1");

        favouriteService.remove("user-id-1", "car-id-1");

        verify(favouriteRepository, times(1)).deleteByUserIdAndCarId("user-id-1", "car-id-1");
    }

    @Test
    @DisplayName("remove - не найден в избранном")
    public void remove_notFound() {
        when(favouriteRepository.existsByUserIdAndCarId("user-id-1", "car-id-1")).thenReturn(false);

        assertThatThrownBy(() -> favouriteService.remove("user-id-1", "car-id-1"))
                .isInstanceOf(FavouriteException.class)
                .hasMessage("Автомобиль не найден в избранном");

        verify(favouriteRepository, never()).deleteByUserIdAndCarId(any(), any());
    }

    /* FIND BY USER ID */

    @Test
    @DisplayName("findByUserId - возвращает список избранного")
    public void findByUserId_success() {
        when(favouriteRepository.findByUserId("user-id-1"))
                .thenReturn(List.of(buildFavourite(), buildFavourite()));

        List<FavouriteResponse> result = favouriteService.findByUserId("user-id-1");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo("user-id-1");
    }

    @Test
    @DisplayName("findByUserId - пустой список")
    public void findByUserId_empty() {
        when(favouriteRepository.findByUserId("user-id-1")).thenReturn(List.of());

        List<FavouriteResponse> result = favouriteService.findByUserId("user-id-1");

        assertThat(result).isEmpty();
    }

    /* IS FAVOURITE */

    @Test
    @DisplayName("isFavourite - в избранном")
    public void isFavourite_true() {
        when(favouriteRepository.existsByUserIdAndCarId("user-id-1", "car-id-1")).thenReturn(true);

        boolean result = favouriteService.isFavourite("user-id-1", "car-id-1");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isFavourite - не в избранном")
    public void isFavourite_false() {
        when(favouriteRepository.existsByUserIdAndCarId("user-id-1", "car-id-1")).thenReturn(false);

        boolean result = favouriteService.isFavourite("user-id-1", "car-id-1");

        assertThat(result).isFalse();
    }
}
