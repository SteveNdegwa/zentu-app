package com.zentu.zentu_core.auth.repository;

import com.zentu.zentu_core.auth.entity.Identity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IdentityRepository extends JpaRepository<Identity, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE Identity i SET i.state = 'INACTIVE' WHERE i.state = 'ACTIVE' AND i.user = :user")
    void deactivateUserIdentities(@Param("user") User user);

    Optional<Identity> findByTokenAndState(String token, State state);

}
