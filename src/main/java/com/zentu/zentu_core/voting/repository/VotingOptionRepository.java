package com.zentu.zentu_core.voting.repository;

import com.zentu.zentu_core.voting.entity.VotingOption;
import com.zentu.zentu_core.voting.entity.VotingProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VotingOptionRepository extends JpaRepository<VotingOption, UUID> {
    List<VotingOption> findAllByVotingProposal(VotingProposal votingProposal);

    Optional<VotingOption> findByVotingProposalAndId(VotingProposal votingProposal, UUID optionId);

    boolean existsByNameAndVotingProposal(String name, VotingProposal votingProposal);
}
