package com.zentu.zentu_core.billing.repository;

import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
	Optional<Account> findByGroup(Group group);
}