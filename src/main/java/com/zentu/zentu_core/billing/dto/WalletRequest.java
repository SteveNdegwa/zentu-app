package com.zentu.zentu_core.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class WalletRequest {

	@NotNull(message = "Group alias must not be null")
	private String alias;

	@NotNull(message = "Group must not be null")
	private Boolean isGroup;

	@NotNull(message = "Receipt must not be null")
	private String receiptNumber;

	@NotNull(message = "Amount must not be null")
	@Positive(message = "Amount must be greater than zero")
	private BigDecimal amount;
}
