package com.zentu.zentu_core.billing.repository;

import com.zentu.zentu_core.billing.entity.BalanceLog;
import com.zentu.zentu_core.billing.entity.BalanceLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BalanceLogEntryRepository extends JpaRepository<BalanceLogEntry, UUID> {
	Optional<BalanceLogEntry> findBybalanceLog(BalanceLog balanceLog);
}
