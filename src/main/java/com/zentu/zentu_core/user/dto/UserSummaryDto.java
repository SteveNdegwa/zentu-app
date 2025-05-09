package com.zentu.zentu_core.user.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String otherName;
}
