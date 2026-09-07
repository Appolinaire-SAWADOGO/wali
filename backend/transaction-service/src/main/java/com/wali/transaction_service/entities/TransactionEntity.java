package com.wali.transaction_service.entities;
import com.wali.transaction_service.enums.TransactionStatus;
import com.wali.transaction_service.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(nullable = false)
    private UUID receiverWalletId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private UUID senderWalletId;

    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private Date createdAt ;
}
