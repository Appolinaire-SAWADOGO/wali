package com.wali.wallet_service.controller;

import com.wali.wallet_service.dto.response.WalletResponseDto;
import com.wali.wallet_service.exceptions.WalletNotFoundException;
import com.wali.wallet_service.response.WalletResponse;
import com.wali.wallet_service.services.wallet.WalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequestMapping("/wallet")
@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/user/{userId}")
    private ResponseEntity<WalletResponse> getWalletByUserId(@PathVariable String userId) {
        log.info("Getting wallet for userId={}", userId);

        try {
            WalletResponseDto walletResponseDto =  walletService.getWalletByUserId(userId);

            log.info("Wallet found successfully for userId={}", userId);

            return ResponseEntity.status(HttpStatus.OK).body(WalletResponse.builder()
                            .data(walletResponseDto)
                    .build());

        }catch (WalletNotFoundException e) {
            log.warn("Wallet not found for userId={}", userId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WalletResponse.builder()
                            .message("Wallet not found")
                            .build());
        }

    }
}
