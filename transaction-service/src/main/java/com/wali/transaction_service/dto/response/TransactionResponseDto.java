package com.wali.transaction_service.dto.response;

import com.wali.transaction_service.enums.TransactionType;
import com.wali.transaction_service.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionResponseDto {
    public String id;

    private String receiverWalletId;

    private String senderWalletId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private Date createdAt ;
}
