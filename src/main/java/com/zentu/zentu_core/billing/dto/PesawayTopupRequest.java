package com.zentu.zentu_core.billing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PesawayTopupRequest {
    @NotNull(message = "Phone number alias must not be null")
    private String phoneNumber;

    @NotNull(message = "Channel must not be null")
    private String channel;

    @NotNull(message = "Reason must not be null")
    private String reason;

    private String groupAlias;

    private String userId;

    @NotNull(message = "Receipt must not be null")
    private String receiptNumber;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be greater than zero")
    private double amount;
}
