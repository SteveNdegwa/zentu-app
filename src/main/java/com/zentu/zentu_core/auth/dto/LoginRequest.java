package com.zentu.zentu_core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Phone number must be provided")
    private String phoneNumber;

    @NotBlank(message = "Password must be provided")
    private String password;
}
