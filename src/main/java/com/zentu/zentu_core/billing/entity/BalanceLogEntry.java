package com.zentu.zentu_core.billing.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.enums.AccountFieldType;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "balance_log_entry")
public class BalanceLogEntry extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "balance_log", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private BalanceLog balanceLog;

	@Enumerated(EnumType.STRING)
	@Column(name = "entry_category", nullable = false)
	@NotNull(message = "Entry Category is required")
	private EntryCategory entryCategory = EntryCategory.DEBIT;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_fileld_type", nullable = false)
	@NotNull(message = "Account Field Type is required")
	private AccountFieldType accountFieldType;
	
	@Column(name = "amount_transacted", precision = 25, scale = 2, nullable = false)
	private BigDecimal amountTransacted = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", precision = 25, scale = 2, nullable = false)
	private State status = State.ACTIVE;
	
}

