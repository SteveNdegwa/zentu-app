package com.zentu.zentu_core.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateGroupRequest {
    @NotBlank(message = "Group's name must be provided")
    private String name;

    private String description;
}
