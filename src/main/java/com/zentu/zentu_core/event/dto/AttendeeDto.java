package com.zentu.zentu_core.event.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendeeDto {
    private UUID userId;
    private String fullName;
    private UUID eventId;
    private String eventName;
    private String state;
}
