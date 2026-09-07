package com.wali.notification_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationResponseDto {
    public UUID id;

    private UUID walletId;

    private String title;

    private String body;

    private boolean read;

    private Date createdAt ;
}
