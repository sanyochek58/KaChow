package com.example.dealer.service;

import com.example.dealer.dto.request.DealerRequest;
import com.example.dealer.dto.response.DealerResponse;
import com.example.dealer.exception.DealerException;
import com.example.dealer.model.Dealer;
import com.example.dealer.repository.DealerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;

    @Override
    public DealerResponse create(DealerRequest request) {
        if(dealerRepository.existsByName(request.getName())){
            throw new DealerException("Автосалон с таким именем уже существует");
        }

        Dealer dealer = Dealer.builder()
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .phone(request.getPhone())
                .description(request.getDescription())
                .build();

        dealerRepository.save(dealer);
        log.info("Зарегистрирован новый автосалон: {}", dealer.getName());

        return buildDealerResponse(dealer);
    }

    @Override
    public DealerResponse update(String id, DealerRequest request) {

        Dealer dealer = dealerRepository.findById(id).orElseThrow(() -> new DealerException("Автосалона не найден"));

        if (!dealer.getName().equals(request.getName()) &&
                dealerRepository.existsByName(request.getName())) {
            throw new DealerException("Автосалон с таким названием уже существует");
        }

        dealer.setName(request.getName());
        dealer.setCity(request.getCity());
        dealer.setAddress(request.getAddress());
        dealer.setPhone(request.getPhone());
        dealer.setDescription(request.getDescription());

        dealerRepository.save(dealer);

        log.info("Автосалон обновлён: {}", dealer.getName());

        return buildDealerResponse(dealer);
    }

    @Override
    public void delete(String id) {
        Dealer dealer = dealerRepository.findById(id).orElseThrow(() -> new DealerException("Автосалона не найден"));
        dealerRepository.deleteById(id);
        log.info("Автосалон удалён: {}", dealer.getName());
    }

    @Override
    public DealerResponse findById(String id) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new DealerException("Автосалон не найден"));

        return buildDealerResponse(dealer);
    }

    @Override
    public List<DealerResponse> findAll(){
        return dealerRepository.findAll().stream().map(this::buildDealerResponse).toList();
    }

    @Override
    public List<DealerResponse> findByCity(String city) {
        return dealerRepository.findByCityIgnoreCase(city).stream().map(this::buildDealerResponse).toList();
    }


    private DealerResponse buildDealerResponse(Dealer dealer) {
        return DealerResponse.builder()
                .id(dealer.getId())
                .name(dealer.getName())
                .city(dealer.getCity())
                .address(dealer.getAddress())
                .phone(dealer.getPhone())
                .description(dealer.getDescription())
                .createdAt(dealer.getCreatedAt())
                .build();
    }
}
