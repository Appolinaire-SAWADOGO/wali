package com.wali.auth_service.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthRegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String userName;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank @Size(min = 8, max = 16)
    private String password;
}
