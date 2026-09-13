package com.wali.transaction_service.request;

import com.wali.transaction_service.enums.TransactionStatus;
import com.wali.transaction_service.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionProcessedRequest {
    public UUID id;

    private UUID receiverWalletId;

    private UUID senderWalletId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private Date createdAt ;
}
