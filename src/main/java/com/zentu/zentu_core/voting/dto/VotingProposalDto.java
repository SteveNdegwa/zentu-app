package com.zentu.zentu_core.voting.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotingProposalDto {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime expiresAt;
    private String creatorName;
    private List<VotingOptionDto> options;
    private String state;
}
