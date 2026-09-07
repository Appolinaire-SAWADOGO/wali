package com.wali.transaction_service.response;

import com.wali.transaction_service.dto.response.TransactionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionResponse {
    private String message;
    private List<TransactionResponseDto> data;
}
