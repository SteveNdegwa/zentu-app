package com.zentu.zentu_core.dto.membership;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMembershipDto {
    private UUID groupId;
    private String groupName;
    private String groupDescription;
    private Boolean isAdmin;
    private String role;
    private LocalDateTime joinedAt;
}
