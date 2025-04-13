package com.zentu.zentu_core.dto.user;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String otherName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Email required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be longer than eight characters")
    private String password;
}
