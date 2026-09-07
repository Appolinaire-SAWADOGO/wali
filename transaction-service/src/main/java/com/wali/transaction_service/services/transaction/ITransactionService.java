package com.wali.transaction_service.services.transaction;


import com.wali.transaction_service.dto.response.TransactionResponseDto;
import com.wali.transaction_service.entities.TransactionEntity;
import com.wali.transaction_service.request.TransactionRequest;

import java.util.List;

public interface ITransactionService {
    void createTransaction(TransactionRequest transactionRequest);
    List<TransactionResponseDto> getTransactionsByWalletId(String walletId);
    TransactionResponseDto transactionEntityToTransactionResponseDto(TransactionEntity transactionEntity);
}
