package com.wali.wallet_service.services.kafka;

import com.wali.wallet_service.Request.TransactionRequest;
import com.wali.wallet_service.services.wallet.WalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Log4j2
@Service
public class KafkaConsumerService {

    @Autowired
    WalletService walletService;

    @Autowired
    JsonMapper jsonMapper;

    @KafkaListener(topics = "user-created", groupId = "wallet-service")
    public void consumeUserCreatedMessage(String message) {

        log.info("Message received from user-created topic: {}", message);

        try {
            walletService.createWallet(message);

            log.info("Wallet created successfully for userId={}", message);
        }catch (Exception e){
            log.error("Failed to create wallet for userId={}", message, e);

            throw e;
        }
    }


    @KafkaListener(topics = "transaction-created", groupId = "wallet-service")
    public void consumeTransactionCreatedMessage(String message) {
        try {
            log.info("Transaction-created event received");

            TransactionRequest transactionRequest =
                    jsonMapper.readValue(message, TransactionRequest.class);

            walletService.processTransaction(transactionRequest);

            log.info("Transaction process successfully for message={}", message);
        } catch (Exception e) {
            log.error("Failed to process transaction event for message",message,e);

            throw e;
        }
    }
}