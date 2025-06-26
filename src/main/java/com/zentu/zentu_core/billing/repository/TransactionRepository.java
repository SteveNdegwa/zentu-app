package com.zentu.zentu_core.billing.repository;

import com.zentu.zentu_core.billing.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
	List<Transaction> findByAlias(String alias);
	Optional<Transaction> findByInternalReference(String InternalReference);
}