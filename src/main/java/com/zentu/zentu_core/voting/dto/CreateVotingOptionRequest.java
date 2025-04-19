package com.zentu.zentu_core.voting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVotingOptionRequest {
    @NotBlank(message = "Voting option name is required")
    private String name;
}
