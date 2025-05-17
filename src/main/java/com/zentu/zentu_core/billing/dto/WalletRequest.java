package com.zentu.zentu_core.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WalletRequest {
	private String alias;
	private BigDecimal amount;
}
