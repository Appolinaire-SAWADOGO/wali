package com.wali.wallet_service.services.wallet;


import com.wali.wallet_service.Request.TransactionRequest;
import com.wali.wallet_service.dto.response.WalletResponseDto;
import com.wali.wallet_service.entities.WalletEntity;
import com.wali.wallet_service.response.TransactionResponse;

public interface IWalletService {
    WalletResponseDto createWallet(String userId);
    WalletResponseDto getWalletByUserId(String userId);

    void processTransaction(TransactionRequest transactionRequest);

    WalletResponseDto walletEntityToWalletResponseDto(WalletEntity walletEntity);

    TransactionResponse transactionRequestToTransactionResponse(TransactionRequest transactionRequest);
}
