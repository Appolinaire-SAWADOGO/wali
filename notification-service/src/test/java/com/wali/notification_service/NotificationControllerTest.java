package com.wali.notification_service;

import com.wali.notification_service.enums.TransactionStatus;
import com.wali.notification_service.enums.TransactionType;

import com.wali.notification_service.request.TransactionProcessedRequest;
import com.wali.notification_service.security.jwt.JwtTokenUtil;
import com.wali.notification_service.services.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    NotificationService notificationService;

    @MockitoBean
    JwtTokenUtil jwtTokenUtil;

    @Test
    void getNotificationsByWalletId() throws Exception {
        UUID receiverWalletId = UUID.randomUUID();
        UUID senderWalletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TransactionProcessedRequest transactionProcessedRequest = TransactionProcessedRequest.builder()
                .id(UUID.randomUUID())
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(new BigDecimal("1000"))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.CONFIRMED)
                .createdAt(new Date())
                .build();

        notificationService.createNotification(transactionProcessedRequest);

        String token = "fake-token";
        String subject = userId + "," + "test@email.com";
        when(jwtTokenUtil.validateAccessToken(token)).thenReturn(true);
        when(jwtTokenUtil.getSubject(token)).thenReturn(subject);

        mockMvc.perform(get("/notification/walletId/" + senderWalletId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }
}
