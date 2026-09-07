package com.wali.notification_service.services.notification;



import com.wali.notification_service.dto.response.NotificationResponseDto;
import com.wali.notification_service.entities.NotificationEntity;
import com.wali.notification_service.request.TransactionProcessedRequest;

import java.util.List;

public interface INotificationService {
    void createNotification(TransactionProcessedRequest transactionProcessedRequest);
    List<NotificationResponseDto> getNotificationsByWalletId(String walletId);
    NotificationResponseDto notificationEntityToNotificationResponseDto(NotificationEntity  notificationEntity);
}
