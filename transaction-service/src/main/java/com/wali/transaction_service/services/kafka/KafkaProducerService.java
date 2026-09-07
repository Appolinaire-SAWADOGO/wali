package com.wali.transaction_service.services.kafka;

import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(
            KafkaTemplate<String, String> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic,
                            String message) {

        log.debug("Creating kafka topic " + topic + " with message: " + message);

        kafkaTemplate.send(topic, message);

        log.debug("Message sent to topic " + topic + " with message: " + message);
    }
}