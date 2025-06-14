package com.zentu.zentu_core.otp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {
    @NotBlank
    private String purpose;

    private String userId;

    private String contact;

    @NotBlank
    private String deliveryMethod;
}
