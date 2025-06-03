package com.zentu.zentu_core.user.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phoneNumber;

    private String otp;
}
