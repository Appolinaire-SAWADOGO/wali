package com.wali.transaction_service;

import com.wali.transaction_service.enums.TransactionType;
import com.wali.transaction_service.repository.TransactionRepository;
import com.wali.transaction_service.request.TransactionRequest;
import com.wali.transaction_service.security.jwt.JwtTokenUtil;
import com.wali.transaction_service.services.kafka.KafkaProducerService;
import com.wali.transaction_service.services.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    JsonMapper jsonMapper;

    @MockitoBean
    JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void cleanDatabase() {transactionRepository.deleteAll();
    }

    @Test
    void getTransactionsByWalletId() throws Exception {
        UUID receiverWalletId = UUID.randomUUID();
        UUID senderWalletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .senderWalletId(senderWalletId.toString())
                .receiverWalletId(receiverWalletId.toString())
                .amount(new BigDecimal("1000"))
                .type(TransactionType.DEPOSIT)
                .build();


        transactionService.createTransaction(transactionRequest);

        String token = "fake-token";
        String subject = userId + "," + "test@email.com";
        when(jwtTokenUtil.validateAccessToken(token)).thenReturn(true);
        when(jwtTokenUtil.getSubject(token)).thenReturn(subject);

        mockMvc.perform(get("/transaction/walletId/" + senderWalletId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void createTransaction() throws Exception {
        UUID receiverWalletId = UUID.randomUUID();
        UUID senderWalletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .senderWalletId(senderWalletId.toString())
                .receiverWalletId(receiverWalletId.toString())
                .amount(new BigDecimal("1000"))
                .type(TransactionType.DEPOSIT)
                .build();


        String token = "fake-token";
        String subject = userId + "," + "test@email.com";
        when(jwtTokenUtil.validateAccessToken(token)).thenReturn(true);
        when(jwtTokenUtil.getSubject(token)).thenReturn(subject);

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsString(transactionRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Transaction created successfully"));
    }
}
