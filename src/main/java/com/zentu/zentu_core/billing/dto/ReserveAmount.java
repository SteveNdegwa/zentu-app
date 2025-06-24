package com.zentu.zentu_core.billing.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReserveAmount {
	@NonNull
	private BigDecimal amount;
	private String communityId;
	private String phoneNumber;
}
