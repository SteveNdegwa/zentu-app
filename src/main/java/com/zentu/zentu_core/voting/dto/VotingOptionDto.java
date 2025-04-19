package com.zentu.zentu_core.voting.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotingOptionDto {
    private UUID id;
    private String name;
    private long voteCount;
}
