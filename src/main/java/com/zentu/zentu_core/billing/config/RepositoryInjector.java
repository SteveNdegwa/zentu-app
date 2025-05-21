package com.zentu.zentu_core.billing.config;

import com.zentu.zentu_core.billing.entity.BalanceLog;
import com.zentu.zentu_core.billing.entity.BalanceLogEntry;
import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.repository.BalanceLogEntryRepository;
import com.zentu.zentu_core.billing.repository.BalanceLogRepository;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RepositoryInjector {
	
	@Autowired private TransactionRepository transactionRepository;
	@Autowired private BalanceLogRepository balanceLogRepository;
	@Autowired private BalanceLogEntryRepository balanceLogEntryRepository;
	
	@PostConstruct
	public void injectRepositories() {
		Transaction.setTransactionRepository(transactionRepository);
		BalanceLog.setBalanceLogRepository(balanceLogRepository);
		BalanceLogEntry.setBalanceLogEntryRepository(balanceLogEntryRepository);
	}
}
