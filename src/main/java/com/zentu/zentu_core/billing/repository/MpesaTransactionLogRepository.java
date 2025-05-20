package com.zentu.zentu_core.billing.repository;

import com.zentu.zentu_core.billing.entity.MpesaTransactionLog;
import com.zentu.zentu_core.billing.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MpesaTransactionLogRepository extends JpaRepository<Transaction, UUID> {
    Optional<MpesaTransactionLog> findByCheckoutRequestId(String checkoutRequestId);
}
