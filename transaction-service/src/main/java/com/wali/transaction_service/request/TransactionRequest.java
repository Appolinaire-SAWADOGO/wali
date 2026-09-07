package com.wali.transaction_service.request;

import com.wali.transaction_service.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
    private String receiverWalletId;

    private String senderWalletId;

    private BigDecimal amount;

    private TransactionType type;
}
