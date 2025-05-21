package com.zentu.zentu_core.billing.config;

import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class TransactionConfig {
	
	@Autowired
	public void setTransactionRepository(TransactionRepository transactionRepository) {
		Transaction.setTransactionRepository(transactionRepository);
	}
}
