package com.wali.auth_service;

import com.wali.auth_service.Request.AuthRegisterRequest;
import com.wali.auth_service.Request.AuthLoginRequest;
import com.wali.auth_service.repository.UserRepository;
import com.wali.auth_service.services.kafka.KafkaProducerService;
import com.wali.auth_service.services.user.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void login() throws Exception {
        AuthRegisterRequest authRegisterRequest = AuthRegisterRequest.builder()
                .userName("test")
                .email("test@gmail.com")
                .password("password")
                .build();

        userService.createUser(authRegisterRequest);

        AuthLoginRequest authLoginRequest = AuthLoginRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(authLoginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").isString())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.message").value("Connection successful"));
    }

    @Test
    void register() throws Exception {
        AuthRegisterRequest authRegisterRequest = AuthRegisterRequest.builder()
                .userName("test")
                .password("password")
                .email("test@gmail.com")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(authRegisterRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").isString())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.message").value("Register successful"));
    }
}