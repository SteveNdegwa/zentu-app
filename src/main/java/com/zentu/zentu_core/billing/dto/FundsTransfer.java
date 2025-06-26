package com.zentu.zentu_core.billing.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class FundsTransfer {
	private BigDecimal amount;
	private String walletAlias;
	private String destination;
	private String phoneNumber;
}
