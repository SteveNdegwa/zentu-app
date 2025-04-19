package com.zentu.zentu_core.voting.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingProposalBasicDto {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime expiresAt;
    private String creatorName;
    private String state;
}

