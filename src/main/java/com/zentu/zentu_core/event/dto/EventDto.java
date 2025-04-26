package com.zentu.zentu_core.event.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private String name;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID groupId;
    private String groupName;
    private UUID creatorId;
    private String creatorName;
    private String state;
    private List<AttendeeDto> attendees;
}
