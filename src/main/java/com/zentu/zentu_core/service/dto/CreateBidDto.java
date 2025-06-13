package com.zentu.zentu_core.service.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateBidDto {
    @NotNull
    private BigDecimal bidAmount;

    private String message;

    private LocalDate preferredDate;

    @NotNull
    private String bidder;
}

