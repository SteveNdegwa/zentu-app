package com.zentu.zentu_core.group.repository;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.group.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {

    Optional<GroupMembership> findByUserAndGroupAndState(User user, Group group, State state);

    List<GroupMembership> findAllByUserAndState(User user, State state);

    List<GroupMembership> findAllByGroupAndState(Group group, State state);

    boolean existsByUserAndGroupAndState(User user, Group group, State state);
}
