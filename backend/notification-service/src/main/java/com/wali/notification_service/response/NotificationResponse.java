package com.wali.notification_service.response;

import com.wali.notification_service.dto.response.NotificationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationResponse {
    private String message;
    private List<NotificationResponseDto> data;
}
