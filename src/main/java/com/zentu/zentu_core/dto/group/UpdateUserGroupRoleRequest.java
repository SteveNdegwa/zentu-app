package com.zentu.zentu_core.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserGroupRoleRequest {
    @NotBlank(message = "Role is required")
    private String role;
}
