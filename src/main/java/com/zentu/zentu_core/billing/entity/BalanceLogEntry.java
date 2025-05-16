package com.zentu.zentu_core.billing.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import jakarta.persistence.*;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entry_type_id", referencedColumnName = "id", nullable = false)
	private EntryType entryType;
	
	@Column(name = "entry_type", length = 100)
	private String entryTypeName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_field_type_id", referencedColumnName = "id", nullable = false)
	private AccountFieldType accountFieldType;
	
	@Column(name = "account_field", length = 100)
	private String accountFieldTypeName;
	
	@Column(name = "amount_transacted", precision = 25, scale = 2, nullable = false)
	private BigDecimal amountTransacted = BigDecimal.ZERO;

	
	@Column(name = "state_name", length = 100)
	private String stateName;
	
}

