package com.zentu.zentu_core.group.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupRoleRequest {
    @NotBlank
    private String role;
}
