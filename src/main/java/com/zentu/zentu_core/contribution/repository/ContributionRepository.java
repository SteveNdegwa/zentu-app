package com.zentu.zentu_core.contribution.repository;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.contribution.entity.Contribution;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContributionRepository extends JpaRepository<Contribution, UUID> {
    Optional<Contribution> findByIdAndState(UUID id, State state);

    List<Contribution> findAllByNameAndGroupIdAndState(String name, String groupAlias, State state);

    Optional<Contribution> findByAliasAndState(String alias, State state);

    List<Contribution> findAllByCreatorIdAndState(String creatorId, State state);

    List<Contribution> findAllByGroupIdAndState(String groupAlias, State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Contribution> findTopByOrderByAliasDesc();
}
