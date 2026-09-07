package com.wali.auth_service.dto.response;

import com.wali.auth_service.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponseDto {
    private UUID id;
    private String userName;
    private  String  email;
    private UserStatus status ;
    private Date createAt;
}
