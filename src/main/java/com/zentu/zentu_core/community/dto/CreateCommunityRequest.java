package com.zentu.zentu_core.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommunityRequest {
    @NotBlank
    private String name;

    private String description;
}
