package com.example.car.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic carPriceChangedTopic() {
        return TopicBuilder.name("car.price.changed")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic carStatusChangedTopic() {
        return TopicBuilder.name("car.status.changed")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
