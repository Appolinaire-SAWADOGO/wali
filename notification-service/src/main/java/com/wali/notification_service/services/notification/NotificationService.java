package com.wali.notification_service.services.notification;

import com.wali.notification_service.dto.response.NotificationResponseDto;
import com.wali.notification_service.entities.NotificationEntity;
import com.wali.notification_service.enums.TransactionStatus;
import com.wali.notification_service.enums.TransactionType;
import com.wali.notification_service.repository.NotificationRepository;
import com.wali.notification_service.request.TransactionProcessedRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;

    private final JsonMapper jsonMapper;

    @Override
    public void createNotification(TransactionProcessedRequest transactionProcessedRequest) {
        log.debug(
                "Creating notification for senderWalletId={} and receiverWalletId={}",
                transactionProcessedRequest.getSenderWalletId(),
                transactionProcessedRequest.getReceiverWalletId()
        );

        NotificationEntity senderNotificationEntity = NotificationEntity.builder()
                .walletId(transactionProcessedRequest.getSenderWalletId())
                .read(false)
                .createdAt(new Date())
                .build();

        NotificationEntity receiverNotificationEntity = NotificationEntity.builder()
                .walletId(transactionProcessedRequest.getReceiverWalletId())
                .read(false)
                .createdAt(new Date())
                .build();

        if(transactionProcessedRequest.getStatus() == TransactionStatus.CONFIRMED){
            if(transactionProcessedRequest.getType() == TransactionType.DEPOSIT){
                senderNotificationEntity.setTitle("Dépôt effectué 💰");
                senderNotificationEntity.setBody(
                        "Votre compte a été crédité de "
                                + transactionProcessedRequest.getAmount()
                                + " FCFA."
                );
            }else {
                senderNotificationEntity.setTitle("Transfert effectué 💸");
                senderNotificationEntity.setBody(
                        "Votre transfert de "
                                + transactionProcessedRequest.getAmount()
                                + " FCFA vers le portefeuille "
                                + transactionProcessedRequest.getReceiverWalletId()
                                + " a été effectué avec succès."
                );

                receiverNotificationEntity.setTitle("Argent reçu 💰");
                receiverNotificationEntity.setBody(
                        "Vous avez reçu "
                                + transactionProcessedRequest.getAmount()
                                + " FCFA du portefeuille "
                                + transactionProcessedRequest.getSenderWalletId()
                                + "."
                );
            }
        }else if(transactionProcessedRequest.getStatus() == TransactionStatus.FAILED){
            if (transactionProcessedRequest.getType() == TransactionType.DEPOSIT) {
                senderNotificationEntity.setTitle("Dépôt échoué ❌");
                senderNotificationEntity.setBody(
                        "Votre dépôt de "
                                + transactionProcessedRequest.getAmount()
                                + " FCFA a échoué. Veuillez réessayer."
                );
            } else {
                senderNotificationEntity.setTitle("Transfert échoué ❌");
                senderNotificationEntity.setBody(
                        "Votre transfert de "
                                + transactionProcessedRequest.getAmount()
                                + " FCFA vers le portefeuille "
                                + transactionProcessedRequest.getReceiverWalletId()
                                + " a échoué."
                );
            }
        }

        notificationRepository.save(senderNotificationEntity);
        if(transactionProcessedRequest.getType() == TransactionType.TRANSFER
            && transactionProcessedRequest.getStatus() == TransactionStatus.CONFIRMED){
            notificationRepository.save(receiverNotificationEntity);
        }

        log.debug("{} notification created for senderWalletId={} and receiverWalletId={}",
                transactionProcessedRequest.getSenderWalletId(),
                transactionProcessedRequest.getReceiverWalletId());
    }

    @Override
    public List<NotificationResponseDto> getNotificationsByWalletId(String walletId) {
        log.debug("Searching notifications for walletId={}", walletId);

        List<NotificationResponseDto> notificationResponseDtos = notificationRepository.findAllByWalletId(UUID.fromString(walletId))
                .stream().map(transactionEntity ->
                        notificationEntityToNotificationResponseDto(transactionEntity))
                .collect(Collectors.toList());

        log.debug("Notifications founds for walletId={}", walletId);

        return notificationResponseDtos;
    }

    @Override
    public NotificationResponseDto notificationEntityToNotificationResponseDto(NotificationEntity notificationEntity) {
        NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder()
                .id(notificationEntity.getId())
                .walletId(notificationEntity.getWalletId())
                .title(notificationEntity.getTitle())
                .body(notificationEntity.getBody())
                .read(notificationEntity.isRead())
                .createdAt(notificationEntity.getCreatedAt())
                .build();

        return notificationResponseDto;
    }
}