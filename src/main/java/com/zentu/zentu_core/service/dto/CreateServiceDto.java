package com.zentu.zentu_core.service.dto;

import com.zentu.zentu_core.service.enums.ServiceType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateServiceDto {
    @NotBlank
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private String provider;

    @NotNull
    private ServiceType serviceType;

}