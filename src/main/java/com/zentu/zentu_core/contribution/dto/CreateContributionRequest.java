package com.zentu.zentu_core.contribution.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateContributionRequest {
    @NotBlank
    private String name;

    private String groupId;

    private String groupName;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;
}
