package com.example.dealer.service;

import com.example.dealer.dto.request.DealerRequest;
import com.example.dealer.dto.response.DealerResponse;
import com.example.dealer.exception.DealerException;
import com.example.dealer.model.Dealer;
import com.example.dealer.repository.DealerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DealerServiceImplTest {

    @InjectMocks
    private DealerServiceImpl dealerService;

    @Mock
    private DealerRepository dealerRepository;

    // Вспомогательный метод — чтобы не дублировать создание запроса
    private DealerRequest buildRequest() {
        DealerRequest request = new DealerRequest();
        request.setName("АвтоМир");
        request.setCity("Москва");
        request.setAddress("ул. Ленина, 1");
        request.setPhone("+79991234567");
        request.setDescription("Описание");
        return request;
    }

    // Вспомогательный метод — создание объекта Dealer
    private Dealer buildDealer() {
        return Dealer.builder()
                .id("dealer-id-1")
                .name("АвтоМир")
                .city("Москва")
                .address("ул. Ленина, 1")
                .phone("+79991234567")
                .description("Описание")
                .build();
    }

    /* CREATE */

    @Test
    @DisplayName("Создание автосалона - успешно")
    public void createDealer_success() {
        // given
        DealerRequest request = buildRequest();

        when(dealerRepository.existsByName("АвтоМир")).thenReturn(false);
        when(dealerRepository.save(any(Dealer.class))).thenAnswer(i -> {
            Dealer d = i.getArgument(0);
            d.setId("dealer-id-1");
            return d;
        });

        // when
        DealerResponse response = dealerService.create(request);

        // then
        assertThat(response.getName()).isEqualTo("АвтоМир");
        assertThat(response.getCity()).isEqualTo("Москва");
        verify(dealerRepository).save(any(Dealer.class));
    }

    @Test
    @DisplayName("Создание автосалона - название занято")
    public void createDealer_nameAlreadyExists() {
        // given
        DealerRequest request = buildRequest();
        when(dealerRepository.existsByName("АвтоМир")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> dealerService.create(request))
                .isInstanceOf(DealerException.class)
                .hasMessageContaining("уже существует");

        verify(dealerRepository, never()).save(any());
    }

    /* UPDATE */

    @Test
    @DisplayName("Обновление автосалона - успешно")
    public void updateDealer_success() {
        // given
        Dealer dealer = buildDealer();
        DealerRequest request = buildRequest();
        request.setName("НовоеИмя");

        when(dealerRepository.findById("dealer-id-1")).thenReturn(Optional.of(dealer));
        when(dealerRepository.existsByName("НовоеИмя")).thenReturn(false);
        when(dealerRepository.save(any(Dealer.class))).thenReturn(dealer);

        // when
        DealerResponse response = dealerService.update("dealer-id-1", request);

        // then
        assertThat(response).isNotNull();
        verify(dealerRepository).save(any(Dealer.class));
    }

    @Test
    @DisplayName("Обновление автосалона - не найден")
    public void updateDealer_notFound() {
        // given
        when(dealerRepository.findById("unknown-id")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> dealerService.update("unknown-id", buildRequest()))
                .isInstanceOf(DealerException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    @DisplayName("Обновление автосалона - новое название занято")
    public void updateDealer_nameAlreadyExists() {
        // given
        Dealer dealer = buildDealer();
        DealerRequest request = buildRequest();
        request.setName("ДругойСалон");

        when(dealerRepository.findById("dealer-id-1")).thenReturn(Optional.of(dealer));
        when(dealerRepository.existsByName("ДругойСалон")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> dealerService.update("dealer-id-1", request))
                .isInstanceOf(DealerException.class)
                .hasMessageContaining("уже существует");

        verify(dealerRepository, never()).save(any());
    }

    /* DELETE */

    @Test
    @DisplayName("Удаление автосалона - успешно")
    public void deleteDealer_success() {
        // given
        Dealer dealer = buildDealer();
        when(dealerRepository.findById("dealer-id-1")).thenReturn(Optional.of(dealer));

        // when
        dealerService.delete("dealer-id-1");

        // then
        verify(dealerRepository).deleteById("dealer-id-1");
    }

    @Test
    @DisplayName("Удаление автосалона - не найден")
    public void deleteDealer_notFound() {
        // given
        when(dealerRepository.findById("unknown-id")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> dealerService.delete("unknown-id"))
                .isInstanceOf(DealerException.class)
                .hasMessageContaining("не найден");

        verify(dealerRepository, never()).deleteById(anyString());
    }

    /* FIND */

    @Test
    @DisplayName("Поиск автосалона по id - успешно")
    public void findById_success() {
        // given
        Dealer dealer = buildDealer();
        when(dealerRepository.findById("dealer-id-1")).thenReturn(Optional.of(dealer));

        // when
        DealerResponse response = dealerService.findById("dealer-id-1");

        // then
        assertThat(response.getId()).isEqualTo("dealer-id-1");
        assertThat(response.getName()).isEqualTo("АвтоМир");
    }

    @Test
    @DisplayName("Поиск автосалона по id - не найден")
    public void findById_notFound() {
        // given
        when(dealerRepository.findById("unknown-id")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> dealerService.findById("unknown-id"))
                .isInstanceOf(DealerException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    @DisplayName("Получить все автосалоны - успешно")
    public void findAll_success() {
        // given
        when(dealerRepository.findAll()).thenReturn(List.of(buildDealer(), buildDealer()));

        // when
        List<DealerResponse> result = dealerService.findAll();

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Получить автосалоны по городу - успешно")
    public void findByCity_success() {
        // given
        when(dealerRepository.findByCityIgnoreCase("Москва"))
                .thenReturn(List.of(buildDealer()));

        // when
        List<DealerResponse> result = dealerService.findByCity("Москва");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Москва");
    }
}