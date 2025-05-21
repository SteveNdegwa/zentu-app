package com.zentu.zentu_core.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCallbackRequest {
    @NotBlank
    private String unique_identifier;

    @NotBlank
    private String status;

    private String message;

    private LocalDateTime sent_time;
}
