package com.zentu.zentu_core.contribution.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateContributionRequest {
    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;
}
