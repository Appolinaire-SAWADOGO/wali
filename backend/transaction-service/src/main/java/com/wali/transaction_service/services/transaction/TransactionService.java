package com.wali.transaction_service.services.transaction;

import com.wali.transaction_service.dto.response.TransactionResponseDto;
import com.wali.transaction_service.entities.TransactionEntity;
import com.wali.transaction_service.enums.TransactionStatus;
import com.wali.transaction_service.exceptions.TransactionNotFoundException;
import com.wali.transaction_service.repository.TransactionRepository;
import com.wali.transaction_service.request.TransactionProcessedRequest;
import com.wali.transaction_service.request.TransactionRequest;
import com.wali.transaction_service.services.kafka.KafkaProducerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;

    private final KafkaProducerService kafkaProducerService;

    private final JsonMapper jsonMapper;

    @Override
    public void createTransaction(TransactionRequest transactionRequest) {
        log.debug(
                "Creating {} transaction for senderWalletId={} and receiverWalletId={}",
                transactionRequest.getType(),
                transactionRequest.getSenderWalletId(),
                transactionRequest.getReceiverWalletId()
        );

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .senderWalletId(UUID.fromString(transactionRequest.getSenderWalletId()))
                .receiverWalletId(UUID.fromString(transactionRequest.getReceiverWalletId()))
                .amount(transactionRequest.getAmount())
                .type(transactionRequest.getType())
                .status(TransactionStatus.PENDING)
                .createdAt(new Date())
                .build();

        TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);

        String message = JsonMapper.shared().writeValueAsString(savedTransaction);

        kafkaProducerService.sendMessage("transaction-created", message);

        log.debug("'transaction-created' topic sent", message);

        log.debug("{} transaction created for senderWalletId={} and receiverWalletId={}",
                transactionRequest.getType(),
                transactionRequest.getSenderWalletId(),
                transactionRequest.getReceiverWalletId());
    }

    @Override
    public List<TransactionResponseDto> getTransactionsByWalletId(String walletId) {
        log.debug("Searching transactions for walletId={}", walletId);

        List<TransactionResponseDto> transactionResponseDtos = transactionRepository.findAllByWalletId(UUID.fromString(walletId))
                .stream().map(transactionEntity ->
                        transactionEntityToTransactionResponseDto(transactionEntity))
                .collect(Collectors.toList());

        log.debug("Transactions founds for walletId={}", walletId);

        return transactionResponseDtos;
    }

    @Transactional
    public void updateTransactionStatus(TransactionProcessedRequest transactionProcessedRequest) {
        log.debug("Updating transactions for id={}", transactionProcessedRequest.getId());

        if(!transactionRepository.existsById(transactionProcessedRequest.getId())){
            log.warn("Transaction not found for id={}", transactionProcessedRequest.getId());
            throw new TransactionNotFoundException("Transaction not found for id=" + transactionProcessedRequest.getId());
        }

        transactionRepository.updateStatusById(transactionProcessedRequest.getId(), transactionProcessedRequest.getStatus());

        log.debug("Transactions updated for id={}", transactionProcessedRequest.getId());
    }

    @Override
    public TransactionResponseDto transactionEntityToTransactionResponseDto(TransactionEntity transactionEntity) {
        TransactionResponseDto transactionResponseDto = TransactionResponseDto.builder()
                .id(transactionEntity.getId().toString())
                .senderWalletId(transactionEntity.getSenderWalletId().toString())
                .receiverWalletId(transactionEntity.getReceiverWalletId().toString())
                .amount(transactionEntity.getAmount())
                .type(transactionEntity.getType())
                .status(transactionEntity.getStatus())
                .createdAt(transactionEntity.getCreatedAt())
                .build();

        return transactionResponseDto;
    }

}
