package com.example.dealer.repository;

import com.example.dealer.model.Dealer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class DealerRepositoryTest {

    @Autowired
    private DealerRepository dealerRepository;

    @BeforeEach
    public void beforeEach() {
        dealerRepository.deleteAll();
    }

    @Test
    @DisplayName("findByEmail - находит автосалон по имени")
    void findByName_found(){

        // given

        Dealer dealer = Dealer.builder()
                .name("Автомир")
                .city("Москва")
                .address("ул. Ленина, 1")
                .phone("+79991234567")
                .description("Описание")
                .build();

        dealerRepository.save(dealer);

        // when
        Optional<Dealer> result = dealerRepository.findByName("Автомир");

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Москва");
        assertThat(result.get().getName()).isEqualTo("Автомир");
        assertThat(result.get().getPhone()).isEqualTo("+79991234567");

    }

    @Test
    @DisplayName("findByEmail - не находит автосалон по имени")
    void findByName_not_found(){
        Optional<Dealer> result = dealerRepository.findByName("test@mail.ru");
        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("existsByName - запись существует")
    void  existsByName_found(){

        //given
        Dealer dealer = Dealer.builder()
                .name("Автомир")
                .city("Москва")
                .address("ул. Ленина, 1")
                .phone("+79991234567")
                .description("Описание")
                .build();

        dealerRepository.save(dealer);

        //when / then

        assertThat(dealerRepository.existsByName("Автомир")).isTrue();
    }

    @Test
    @DisplayName("existsByName - если записи нет")
    void existsByName_not_found(){
        assertThat(dealerRepository.existsByName("test_avto")).isFalse();
    }

    @Test
    @DisplayName("findByCityIgnoreCase - находит автосалоны по городу")
    void findByCityIgnoreCase_found() {
        // given
        Dealer dealer1 = Dealer.builder()
                .name("Автомир")
                .city("Москва")
                .address("ул. Ленина, 1")
                .phone("+79991234567")
                .build();

        Dealer dealer2 = Dealer.builder()
                .name("АвтоПлюс")
                .city("Москва")
                .address("ул. Пушкина, 5")
                .phone("+79997654321")
                .build();

        Dealer dealer3 = Dealer.builder()
                .name("СПбАвто")
                .city("Санкт-Петербург")
                .address("Невский пр., 1")
                .phone("+79991111111")
                .build();

        dealerRepository.saveAll(List.of(dealer1, dealer2, dealer3));

        // when
        List<Dealer> result = dealerRepository.findByCityIgnoreCase("Москва");

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).allMatch(d -> d.getCity().equals("Москва"));
    }

    @Test
    @DisplayName("findByCityIgnoreCase - регистр не важен")
    void findByCityIgnoreCase_caseInsensitive() {
        // given
        Dealer dealer = Dealer.builder()
                .name("Автомир")
                .city("Москва")
                .address("ул. Ленина, 1")
                .phone("+79991234567")
                .build();

        dealerRepository.save(dealer);

        // when — ищем в нижнем регистре
        List<Dealer> result = dealerRepository.findByCityIgnoreCase("москва");

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Автомир");
    }

    @Test
    @DisplayName("findByCityIgnoreCase - пустой список если город не найден")
    void findByCityIgnoreCase_notFound() {
        // when
        List<Dealer> result = dealerRepository.findByCityIgnoreCase("Новосибирск");

        // then
        assertThat(result.size()).isEqualTo(0);
    }


}
