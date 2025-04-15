package com.zentu.zentu_core.group.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private Boolean isAdmin;
    private String role;
    private LocalDateTime joinedAt;
}
