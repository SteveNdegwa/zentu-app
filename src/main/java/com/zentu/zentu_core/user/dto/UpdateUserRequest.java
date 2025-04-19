package com.zentu.zentu_core.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String otherName;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String email;
}
