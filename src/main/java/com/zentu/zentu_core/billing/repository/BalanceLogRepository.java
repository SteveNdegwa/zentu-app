package com.zentu.zentu_core.billing.repository;

import com.zentu.zentu_core.billing.entity.BalanceLog;
import com.zentu.zentu_core.billing.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BalanceLogRepository extends JpaRepository<BalanceLog, UUID> {
	Optional<BalanceLog> findByTransaction(Transaction transaction);
}
