package com.zentu.zentu_core.group.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSummaryDto {
    private UUID id;
    private String name;
    private String description;
}
