package com.zentu.zentu_core.billing.dto;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
public class WalletRequest {
	@NotNull(message = "Group alias must not be null")
	private String groupAlias;
	
	@NotNull(message = "Group alias must not be null")
	private String phoneNumber;
	
	
	@NotNull(message = "Receipt must not be null")
	private String receiptNumber;

	@NotNull(message = "Amount must not be null")
	@Positive(message = "Amount must be greater than zero")
	private BigDecimal amount;
}
