package com.zentu.zentu_core.group.repository;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findByIdAndState(UUID id, State state);

    List<Group> findAllByState(State state);

    Optional<Group> findByNameAndState(String name, State state);

    boolean existsByNameAndState(String name, State state);
}