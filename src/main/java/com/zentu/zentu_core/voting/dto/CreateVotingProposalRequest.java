package com.zentu.zentu_core.voting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVotingProposalRequest {
    @NotBlank(message = "Group ID is required")
    private UUID groupId;

    @NotBlank(message = "Voting proposal name is required")
    private String name;

    private String description;

    @NotBlank(message = "Voting proposal expiry time is required")
    private LocalDateTime expiresAt;
}
