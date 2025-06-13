package com.zentu.zentu_core.billing.repository;

import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
	Optional<Account> findByAlias(String alias);

	@Query("SELECT MAX(a.accountNumber) FROM Account a")
	Integer findMaxAccountNumber();
}