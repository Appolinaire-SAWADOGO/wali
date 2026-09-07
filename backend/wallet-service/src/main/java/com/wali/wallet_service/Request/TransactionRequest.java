package com.wali.wallet_service.Request;

import com.wali.wallet_service.enums.TransactionStatus;
import com.wali.wallet_service.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
public class TransactionRequest {
    public UUID id;

    private UUID receiverWalletId;

    private UUID senderWalletId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private Date createdAt ;
}
