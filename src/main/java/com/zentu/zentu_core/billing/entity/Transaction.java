package com.zentu.zentu_core.billing.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {
	
	public enum TransactionType {
		DR, CR
	}
	
	public enum TransactionStatus {
		PENDING, COMPLETED, FAILED
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "receipt_number", nullable = false, unique = true)
	@NotNull(message = "Receipt number is required")
	private String receiptNumber;
	
	@Column(name = "amount", nullable = false)
	@NotNull(message = "Amount is required")
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false)
	@NotNull(message = "Transaction type is required")
	private TransactionType transactionType;
	
	@Column(name = "balance", nullable = false)
	private BigDecimal balance;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@NotNull(message = "Transaction status is required")
	private TransactionStatus status;

}

