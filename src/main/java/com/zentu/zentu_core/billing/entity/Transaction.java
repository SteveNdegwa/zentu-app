package com.zentu.zentu_core.billing.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import com.zentu.zentu_core.billing.enums.TransactionStatus;
import com.zentu.zentu_core.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull(message = "User is required")
	private User user;

	@Column(name = "receipt_number", nullable = false, unique = true)
	@NotNull(message = "Receipt number is required")
	private String receiptNumber;

	@Column(name = "internal_reference", nullable = false, unique = true)
	@NotNull(message = "Internal Reference number is required")
	private String internalReference;

	@Column(name = "amount", nullable = false)
	@NotNull(message = "Amount is required")
	private BigDecimal amount;

	@Column(name = "balance", nullable = false)
	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false)
	@NotNull(message = "Transaction type is required")
	private EntryCategory transactionType = EntryCategory.DEBIT;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@NotNull(message = "Transaction status is required")
	private TransactionStatus status;

}


