package com.zentu.zentu_core.user.repository;
import com.zentu.zentu_core.user.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
}
