package com.zentu.zentu_core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyUserRequest {
    @NotBlank
    private String token;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String otp;
}
