package com.wali.notification_service.request;

import com.wali.notification_service.enums.TransactionStatus;
import com.wali.notification_service.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


@Data
@Builder
public class TransactionProcessedRequest {
    public UUID id;

    private UUID receiverWalletId;

    private UUID senderWalletId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private Date createdAt ;
}
