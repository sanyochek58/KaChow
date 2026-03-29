package com.example.dealer.service;

import com.example.dealer.dto.request.DealerRequest;
import com.example.dealer.dto.response.DealerResponse;
import com.example.dealer.model.Dealer;

import java.util.List;

public interface DealerService {
    DealerResponse create(DealerRequest request);
    DealerResponse update(String id, DealerRequest request);
    void delete(String name);
    DealerResponse findById(String id);
    List<DealerResponse> findAll();
    List<DealerResponse> findByCity(String city);
}
