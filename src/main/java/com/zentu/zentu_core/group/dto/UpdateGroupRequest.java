package com.zentu.zentu_core.group.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupRequest {
    @NotBlank(message = "Group's name must be provided")
    private String name;

    private String description;
}
