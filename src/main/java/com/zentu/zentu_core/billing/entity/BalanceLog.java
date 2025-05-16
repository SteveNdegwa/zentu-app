package com.zentu.zentu_core.billing.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import jakarta.persistence.*;
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
	@JoinColumn(name = "transaction_history_id", referencedColumnName = "id", nullable = false)
	private Transaction transaction;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "balance_entry_type_id", referencedColumnName = "id", nullable = false)
	private BalanceEntryType balanceEntryType;
	
	@Column(name = "balance_entry_type_name", length = 100)
	private String balanceEntryTypeName;
	
	@Column(name = "receipt", length = 100)
	private String receipt;
	
	@Column(name = "amount_transacted", precision = 25, scale = 2, nullable = false)
	private BigDecimal amountTransacted = BigDecimal.ZERO;
	
	@Column(name = "balance", precision = 25, scale = 2, nullable = false)
	private BigDecimal balance = BigDecimal.ZERO;
	

}

