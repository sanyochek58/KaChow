package com.example.car.kafka.producer;

import com.example.car.kafka.event.CarPriceChangedEvent;
import com.example.car.kafka.event.StatusCarChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarEventProducer {

    private static final String PRICE_TOPIC = "car.price.changed";
    private static final String STATUS_TOPIC = "car.status.changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPriceChangeEvent(CarPriceChangedEvent event) {
        kafkaTemplate.send(PRICE_TOPIC, event.getCarId(), event);
        log.info("Отправлено событие изменения цены: carId={}, {} -> {}",
                event.getCarId(), event.getOldPrice(), event.getNewPrice());
    }

    public void sendStatusChangeEvent(StatusCarChangedEvent event){
        kafkaTemplate.send(STATUS_TOPIC, event.getCarId(), event);
        log.info("Отправлено событие изменения статуса: carId={}, {} -> {}",
                event.getCarId(), event.getOldStatus(), event.getNewStatus());
    }
}
