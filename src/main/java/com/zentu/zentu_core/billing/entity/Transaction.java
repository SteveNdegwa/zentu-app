package com.zentu.zentu_core.billing.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import com.zentu.zentu_core.group.entity.Group;
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

	@Column(name = "group_alias",  nullable = true, unique = true)
	private String groupAlias;

	@Column(name = "user_phone_number",  nullable = true, unique = true)
	private String userPhoneNumber;
	
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
	private State status = State.ACTIVE;
	
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
	
	public static Transaction createCreditTransaction(String user, String group, BigDecimal amount, String receipt, BigDecimal balance) {
		Transaction tx = new Transaction();
		tx.setGroupAlias(group);
		tx.setUserPhoneNumber(user);
		tx.setAmount(amount);
		tx.setTransactionType(EntryCategory.CREDIT);
		tx.setInternalReference(receipt);
		tx.setReceiptNumber(receipt);
		tx.setStatus(State.COMPLETED);
		tx.setBalance(balance);
		return tx;
	}
	
	public static Transaction createDebitTransaction(String user, String group, BigDecimal amount, String receipt, BigDecimal balance) {
		Transaction tx = new Transaction();
		tx.setGroupAlias(group);
		tx.setUserPhoneNumber(user);
		tx.setAmount(amount);
		tx.setTransactionType(EntryCategory.DEBIT);
		tx.setInternalReference(receipt);
		tx.setReceiptNumber(receipt);
		tx.setStatus(State.COMPLETED);
		tx.setBalance(balance);
		return tx;
	}
}

