package com.zentu.zentu_core.billing.repository;
import com.zentu.zentu_core.billing.entity.PesawayTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PesawayTransactionLogRepository  extends JpaRepository<PesawayTransactionLog, UUID> {
    Optional<PesawayTransactionLog>  findByOriginatorReference(String originatorReference);
}
