package com.wali.notification_service.services.kafka;

import com.wali.notification_service.request.TransactionProcessedRequest;
import com.wali.notification_service.services.notification.NotificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Log4j2
@Service
public class KafkaConsumerService {

    @Autowired
    NotificationService notificationService;

    @Autowired
    JsonMapper jsonMapper;

    @KafkaListener(
            topics = "transaction-processed",
            groupId = "notification-service")
    public void consumeTransactionProcessedMessage(String message) {
        log.info("Message received from transaction-processed topic: {}", message);

        try {
            TransactionProcessedRequest transactionProcessedRequest = jsonMapper
                    .readValue(message, TransactionProcessedRequest.class);

            notificationService.createNotification(transactionProcessedRequest);

            log.info("Notification sent successfully for message={}", message);
        }catch (Exception e){
            log.error("Failed to sent notification for message={}", message, e);

            throw e;
        }


    }
}