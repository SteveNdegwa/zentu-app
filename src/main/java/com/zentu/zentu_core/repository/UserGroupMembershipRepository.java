package com.zentu.zentu_core.repository;

import com.zentu.zentu_core.entity.Group;
import com.zentu.zentu_core.entity.User;
import com.zentu.zentu_core.entity.UserGroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserGroupMembershipRepository extends JpaRepository<UserGroupMembership, UUID> {

    Optional<UserGroupMembership> findByUserAndGroup(User user, Group group);

    List<UserGroupMembership> findAllByUser(User user);

    List<UserGroupMembership> findAllByGroup(Group group);

    boolean existsByUserAndGroup(User user, Group group);
}
