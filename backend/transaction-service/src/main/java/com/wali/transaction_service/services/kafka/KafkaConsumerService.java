package com.wali.transaction_service.services.kafka;

import com.wali.transaction_service.request.TransactionProcessedRequest;
import com.wali.transaction_service.services.transaction.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Log4j2
@Service
public class KafkaConsumerService {

    @Autowired
    TransactionService transactionService;

    @Autowired
    JsonMapper jsonMapper;

    @KafkaListener(
            topics = "transaction-processed",
            groupId = "transaction-service")
    public void consumeTransactionProcessedMessage(String message) {
        log.info("Message received from transaction-processed topic: {}", message);

        try {
            TransactionProcessedRequest transactionProcessedRequest = jsonMapper
                    .readValue(message, TransactionProcessedRequest.class);

            transactionService.updateTransactionStatus(transactionProcessedRequest);

            log.info("transaction updated successfully for message={}", message);
        }catch (Exception e){
            log.error("Failed to update transaction for message={}", message, e);

            throw e;
        }
    }
}