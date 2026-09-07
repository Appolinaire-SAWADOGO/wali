package com.wali.notification_service.controller;

import com.wali.notification_service.dto.response.NotificationResponseDto;
import com.wali.notification_service.response.NotificationResponse;
import com.wali.notification_service.services.notification.NotificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequestMapping("/notification")
@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/walletId/{walletId}")
    private ResponseEntity<NotificationResponse> getNotificationsByWalletId(@PathVariable String walletId) {
        log.info("Getting notifications for walletId={}", walletId);

        try {
            List<NotificationResponseDto> walletResponseDto =  notificationService.getNotificationsByWalletId(walletId);

            log.info("Notifications found successfully for walletId={}", walletId);

            return ResponseEntity.status(HttpStatus.OK).body(NotificationResponse.builder()
                            .data(walletResponseDto)
                    .build());

        }catch (Exception ex) {
            log.error("Unexpected error during getting notifications for walletId={}", walletId, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            NotificationResponse.builder().message("Internal Error").build()
                    );
        }

    }
}
