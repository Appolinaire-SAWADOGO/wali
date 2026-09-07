package com.wali.wallet_service;

import com.wali.wallet_service.security.jwt.JwtTokenUtil;
import com.wali.wallet_service.services.wallet.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WalletService walletService;

    @MockitoBean
    JwtTokenUtil jwtTokenUtil;

    @Test
    void getWalletByUserId() throws Exception {
        UUID userId = UUID.randomUUID();

        walletService.createWallet(userId.toString());

        String token = "fake-token";
        String subject = userId + "," + "test@email.com";
        when(jwtTokenUtil.validateAccessToken(token)).thenReturn(true);
        when(jwtTokenUtil.getSubject(token)).thenReturn(subject);

        mockMvc.perform(get("/wallet/user/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").isString())
                .andExpect(jsonPath("$.data.balance").isNumber())
                .andExpect(jsonPath("$.data.createdAt").isString());
    }
}