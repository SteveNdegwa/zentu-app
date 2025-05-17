package com.zentu.zentu_core.billing.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.enums.BalanceEntryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "balance_log")
public class BalanceLog extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id", referencedColumnName = "id", nullable = false)
	private Transaction transaction;

	@Enumerated(EnumType.STRING)
	@Column(name = "balance_entry_type", nullable = false)
	@NotNull(message = "Balance Entry Type is required")
	private BalanceEntryType balanceEntryType = BalanceEntryType.TOPUP_WALLET;
	
	@Column(name = "receipt", length = 100)
	private String receipt;
	
	@Column(name = "amount_transacted", precision = 25, scale = 2, nullable = false)
	private BigDecimal amountTransacted = BigDecimal.ZERO;
	
	@Column(name = "balance", precision = 25, scale = 2, nullable = false)
	private BigDecimal balance = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	private State state = State.ACTIVE;

}

