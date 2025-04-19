package com.zentu.zentu_core.voting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVotingProposalRequest {
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private LocalDateTime expiresAt;
}
