package com.zentu.zentu_core.voting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVotingProposalRequest {
    @NotBlank
    private UUID groupId;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocalDateTime expiresAt;
}
