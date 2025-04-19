package com.zentu.zentu_core.voting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVotingProposalRequest {
    @NotBlank(message = "Voting proposal name must be provided")
    private String name;

    private String description;

    @NotBlank(message = "Voting proposal expiry time is required")
    private LocalDateTime expiresAt;
}
