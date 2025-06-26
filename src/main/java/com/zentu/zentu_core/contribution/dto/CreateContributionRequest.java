package com.zentu.zentu_core.contribution.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateContributionRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String communityId;

    private String communityName;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;

    @NotNull
    private LocalDate deadline;

    private List<String> phoneNumbers; // invite to join group
}
