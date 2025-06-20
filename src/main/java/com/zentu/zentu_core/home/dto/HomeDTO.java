package com.zentu.zentu_core.home.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HomeDTO {
	@NotBlank
	private String alias;
}
