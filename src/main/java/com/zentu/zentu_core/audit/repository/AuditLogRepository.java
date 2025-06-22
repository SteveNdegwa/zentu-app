package com.zentu.zentu_core.audit.repository;

import com.zentu.zentu_core.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {}
