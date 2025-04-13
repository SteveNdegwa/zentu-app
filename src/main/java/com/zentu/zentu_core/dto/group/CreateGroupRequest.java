package com.zentu.zentu_core.dto.group;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupRequest {
    @NotNull(message = "Group's name must be provided")
    private String name;

    private String description;
}
