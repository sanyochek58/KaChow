package com.example.dealer.repository;

import com.example.dealer.model.Dealer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DealerRepository extends MongoRepository<Dealer,String> {

    Optional<Dealer> findByName(String name);

    Boolean existsByName(String name);

    List<Dealer> findByCity(String city);

    List<Dealer> findByCityIgnoreCase(String city);


}
