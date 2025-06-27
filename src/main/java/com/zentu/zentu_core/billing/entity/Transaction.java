package com.zentu.zentu_core.billing.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import com.zentu.zentu_core.common.utils.ChargeCalculator;
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

	@Column(name = "alias",  nullable = true)
	private String alias;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_type",  nullable=true)
	private AccountType accountType;
	
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
	
	@Column(name = "charge", nullable = false)
	private BigDecimal charge = BigDecimal.ZERO;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false)
	@NotNull(message = "Transaction type is required")
	private EntryCategory transactionType = EntryCategory.DEBIT;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private State status = State.PROCESSING;
	
	private static TransactionRepository transactionRepository;
	public static void setTransactionRepository(TransactionRepository repo) {
		transactionRepository = repo;
	}
	
	public Transaction save() {
		if (transactionRepository == null) {
			throw new IllegalStateException("TransactionRepository has not been set.");
		}
		return transactionRepository.save(this);
	}
	
	public static Transaction createCreditTransaction(AccountType accountType, String alias, BigDecimal amount, String receipt, BigDecimal balance, State status) {
		Transaction tx = new Transaction();
		BigDecimal charge = ChargeCalculator.calculateCharge(amount);
		tx.setCharge(charge);
		tx.setAlias(alias);
		tx.setAccountType(accountType);
		tx.setAmount(amount);
		tx.setTransactionType(EntryCategory.CREDIT);
		tx.setInternalReference(receipt);
		tx.setReceiptNumber(receipt);
		tx.setBalance(balance);
		return tx;
	}
	
	public static Transaction createDebitTransaction(AccountType accountType, String alias, BigDecimal amount, String receipt, BigDecimal balance, State status) {
		Transaction tx = new Transaction();
		BigDecimal charge = ChargeCalculator.calculateCharge(amount);
		tx.setCharge(charge);
		tx.setAlias(alias);
		tx.setAccountType(accountType);
		tx.setAmount(amount);
		tx.setTransactionType(EntryCategory.DEBIT);
		tx.setInternalReference(receipt);
		tx.setReceiptNumber(receipt);
		tx.setStatus(status);
		tx.setBalance(balance);
		return tx;
	}
}

