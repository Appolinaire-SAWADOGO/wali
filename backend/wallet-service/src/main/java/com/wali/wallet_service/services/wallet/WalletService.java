package com.wali.wallet_service.services.wallet;

import com.wali.wallet_service.response.TransactionResponse;
import com.wali.wallet_service.Request.TransactionRequest;
import com.wali.wallet_service.dto.response.WalletResponseDto;
import com.wali.wallet_service.entities.WalletEntity;
import com.wali.wallet_service.enums.TransactionStatus;
import com.wali.wallet_service.enums.TransactionType;
import com.wali.wallet_service.exceptions.TransactionStatusIsNotPendingException;
import com.wali.wallet_service.exceptions.WalletAlreadyExistsException;
import com.wali.wallet_service.exceptions.WalletNotFoundException;
import com.wali.wallet_service.repository.WalletRepository;
import com.wali.wallet_service.services.kafka.KafkaProducerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class WalletService implements IWalletService {
    private final WalletRepository walletRepository;

    private final KafkaProducerService kafkaProducerService;

    private final JsonMapper jsonMapper;

    @Override
    public WalletResponseDto createWallet(String userId) {
        log.debug("Creating wallet for userId={}", userId);

        boolean walletAlreadyExist = walletRepository.existsByUserId(UUID.fromString(userId));

        if (walletAlreadyExist) {
            log.warn("Wallet already exists for userId={}", userId);
            throw new WalletAlreadyExistsException("Wallet  userId " + userId + " already exists");
        }

        WalletEntity walletEntity = WalletEntity.builder()
                .userId(UUID.fromString(userId))
                .balance(new BigDecimal(0))
                .createdAt(new Date())
                .build();

        WalletEntity saveWallet = walletRepository.save(walletEntity);

        WalletResponseDto walletResponseDto = walletEntityToWalletResponseDto(saveWallet);

        log.debug("Wallet created for userId={}", userId);

        return walletResponseDto;
    }

    @Override
    @Transactional
    public void processTransaction(TransactionRequest transactionRequest){
        log.debug("Process {} transaction for senderWalletId={} and receiverWalletId={}",
                transactionRequest.getType(),
                transactionRequest.getSenderWalletId(),
                transactionRequest.getReceiverWalletId()
        );

        TransactionResponse transactionResponse = transactionRequestToTransactionResponse(
                transactionRequest);
        String message = jsonMapper.writeValueAsString(transactionResponse);


        if(transactionRequest.getStatus() != TransactionStatus.PENDING){
            log.warn("Transaction id={} status is not PENDING",
                    transactionRequest.getId());

            throw new TransactionStatusIsNotPendingException(
                    "transaction " + transactionRequest.getId() + " status is not PENDING");
        }

        WalletEntity senderWalletEntity = walletRepository
                .findById(transactionRequest.getSenderWalletId()).orElseThrow(() -> {
                    log.warn("Wallet for senderWalletId={} not found",
                            transactionRequest.getSenderWalletId());

                    transactionResponse.setStatus(TransactionStatus.FAILED);
                    kafkaProducerService.sendMessage("transaction-processed", message );
                    log.debug("'transaction-processed' topic sent for transactionId={}", transactionRequest.getId() );

                    throw new WalletNotFoundException("Wallet for senderWalletId=" + transactionRequest.getSenderWalletId() + " not found");
                });

        WalletEntity receiverWalletEntity = walletRepository
                .findById(transactionRequest.getReceiverWalletId()).orElseThrow(() -> {
                    log.warn("Wallet for receiverWalletId={} not found",
                            transactionRequest.getReceiverWalletId());

                    transactionResponse.setStatus(TransactionStatus.FAILED);
                    kafkaProducerService.sendMessage("transaction-processed",message );
                    log.debug("'transaction-processed' topic sent for transactionId={}", transactionRequest.getId() );

                    throw new WalletNotFoundException("Wallet for receiverWalletId=" + transactionRequest.getReceiverWalletId() + " not found");
                });

        if(transactionRequest.getType() != TransactionType.DEPOSIT){
                if(senderWalletEntity.getBalance().compareTo(transactionRequest.getAmount()) <= 0){
                    log.debug("Process transaction failed for senderWalletId={} and receiverWalletId={}",
                            transactionRequest.getSenderWalletId(),
                            transactionRequest.getReceiverWalletId()
                    );

                    transactionResponse.setStatus(TransactionStatus.FAILED);
                    kafkaProducerService.sendMessage("transaction-processed", message );
                    log.debug("'transaction-processed' topic sent for transactionId={}", transactionRequest.getId() );
                    return;
                }

            walletRepository.updateWalletBalanceByWalletId(senderWalletEntity.getId(), transactionRequest.getAmount().negate());
        }

        walletRepository.updateWalletBalanceByWalletId(receiverWalletEntity.getId(), transactionRequest.getAmount());

        transactionResponse.setStatus(TransactionStatus.CONFIRMED);
        kafkaProducerService.sendMessage("transaction-processed", message);
        log.debug("'transaction-processed' topic sent for transactionId={}", transactionRequest.getId() );

        log.debug("process transaction success for senderWalletId={} and receiverWalletId={}",
                transactionRequest.getSenderWalletId(),
                transactionRequest.getReceiverWalletId());
    }

    @Override
    public WalletResponseDto getWalletByUserId(String userId) {
        log.debug("Searching wallet for userId={}", userId);

        WalletEntity walletEntity = walletRepository.findByUserId(UUID.fromString(userId)).orElseThrow(
                () -> {
                    log.warn("Wallet not found for userId={}", userId);
                    return  new WalletNotFoundException("Wallet userId " + userId + "  does not exist");
                });

        WalletResponseDto walletResponseDto = walletEntityToWalletResponseDto(walletEntity);

        log.debug("Wallet found for userId={}", userId);

        return walletResponseDto;
    }

    @Override
    public WalletResponseDto walletEntityToWalletResponseDto(WalletEntity walletEntity) {
        WalletResponseDto walletResponseDto = WalletResponseDto.builder()
                .id(walletEntity.getId().toString())
                .balance(walletEntity.getBalance())
                .createdAt(walletEntity.getCreatedAt())
                .build();

        return walletResponseDto;
    }


    @Override
    public TransactionResponse transactionRequestToTransactionResponse(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(transactionRequest.getId())
                .senderWalletId(transactionRequest.getSenderWalletId())
                .receiverWalletId(transactionRequest.getReceiverWalletId())
                .amount(transactionRequest.getAmount())
                .createdAt(transactionRequest.getCreatedAt())
                .build();

        return transactionResponse ;
    }

}
