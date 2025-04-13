package com.zentu.zentu_core.dto.group;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserGroupRoleRequest {
    @NotNull(message = "Role is required")
    private String role;
}
