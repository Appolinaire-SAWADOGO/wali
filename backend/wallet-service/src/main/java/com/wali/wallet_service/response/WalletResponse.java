package com.wali.wallet_service.response;

import com.wali.wallet_service.dto.response.WalletResponseDto;
import com.wali.wallet_service.entities.WalletEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WalletResponse {
    private String message;
    private WalletResponseDto data;
}
