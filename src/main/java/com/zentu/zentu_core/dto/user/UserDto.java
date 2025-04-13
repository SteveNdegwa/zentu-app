package com.zentu.zentu_core.dto.user;

import com.zentu.zentu_core.dto.membership.GroupMembershipDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String otherName;
    private String phoneNumber;
    private String email;
    private String role;
    private Boolean isSuperUser;
    private List<GroupMembershipDto> groupMemberships;
}
