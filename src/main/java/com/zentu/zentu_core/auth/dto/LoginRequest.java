package com.zentu.zentu_core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}
