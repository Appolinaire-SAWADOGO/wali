package com.wali.auth_service;

import com.wali.auth_service.Request.AuthRegisterRequest;
import com.wali.auth_service.entities.UserEntity;
import com.wali.auth_service.repository.UserRepository;
import com.wali.auth_service.security.jwt.JwtTokenUtil;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void getUserById() throws Exception {
     AuthRegisterRequest authRegisterRequest = AuthRegisterRequest.builder()
                .userName("test")
                .email("test@gmail.com")
                .password("test@123")
             .build();

        UserEntity userEntity = userService.createUser(authRegisterRequest);

        String jwtToken = jwtTokenUtil.generateAccessToken(userEntity);

        mockMvc.perform(
                get("/user/" + userEntity.getId().toString())
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").isString())
                .andExpect(jsonPath("$.data.userName").value("test"))
                .andExpect(jsonPath("$.data.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.createAt").isString());
    }
}
