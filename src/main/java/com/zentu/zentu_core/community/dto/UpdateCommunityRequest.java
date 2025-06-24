package com.zentu.zentu_core.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommunityRequest {
    @NotBlank
    private String name;

    private String description;
}
