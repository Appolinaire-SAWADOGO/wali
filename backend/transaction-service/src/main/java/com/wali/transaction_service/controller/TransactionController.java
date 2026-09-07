package com.wali.transaction_service.controller;

import com.wali.transaction_service.dto.response.TransactionResponseDto;
import com.wali.transaction_service.request.TransactionRequest;
import com.wali.transaction_service.response.TransactionResponse;
import com.wali.transaction_service.services.transaction.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequestMapping("/transaction")
@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/walletId/{walletId}")
    private ResponseEntity<TransactionResponse> getTransactionsByWalletId(@PathVariable String walletId) {
        log.info("Getting transactions for walletId={}", walletId);

        try {
            List<TransactionResponseDto> walletResponseDto =  transactionService.getTransactionsByWalletId(walletId);

            log.info("Transactions found successfully for walletId={}", walletId);

            return ResponseEntity.status(HttpStatus.OK).body(TransactionResponse.builder()
                            .data(walletResponseDto)
                    .build());

        }catch (Exception ex) {
            log.error("Unexpected error during getting transactions for walletId={}", walletId, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            TransactionResponse.builder().message("Internal Error").build()
                    );
        }

    }

    @PostMapping()
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        log.info("Transaction attempt for senderWalletId={} and receiverWalletId={}",
                transactionRequest.getSenderWalletId(),
                transactionRequest.getReceiverWalletId());

        try{
            transactionService.createTransaction(transactionRequest);

            log.info("Transaction created successfully  for senderWalletId={} and receiverWalletId={}",
                    transactionRequest.getSenderWalletId() ,
                    transactionRequest.getReceiverWalletId());

            return  ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.builder()
                    .message("Transaction created successfully")
                    .build());
        }catch (Exception ex) {
            log.error("Unexpected error during creating transactions for senderWalletId={} and receiverWalletId={}",
                    transactionRequest.getSenderWalletId(),
                    transactionRequest.getReceiverWalletId(), ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            TransactionResponse.builder().message("Internal Error").build()
                    );
        }
    }
}
