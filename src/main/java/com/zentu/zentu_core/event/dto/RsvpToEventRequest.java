package com.zentu.zentu_core.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RsvpToEventRequest {
    @NotBlank
    private String state;
}
