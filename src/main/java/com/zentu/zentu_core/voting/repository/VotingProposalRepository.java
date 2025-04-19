package com.zentu.zentu_core.voting.repository;

import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.voting.entity.VotingProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VotingProposalRepository extends JpaRepository<VotingProposal, UUID> {
    List<VotingProposal> findAllByCreator(User user);

    List<VotingProposal> findAllByGroup(Group group);

    List<VotingProposal> findAllByGroupIn(List<Group> groups);
}
