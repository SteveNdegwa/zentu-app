package com.zentu.zentu_core.voting.repository;

import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.voting.entity.Vote;
import com.zentu.zentu_core.voting.entity.VotingOption;
import com.zentu.zentu_core.voting.entity.VotingProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    List<Vote> findAllByVotingProposal(VotingProposal votingProposal);

    List<Vote> findAllByVotingOption(VotingOption votingOption);

    List<Vote> findAllByUser(User user);

    Optional<Vote> findByVotingProposalIdAndUserId(UUID proposalId, UUID userId);

    @Query("""
            SELECT v.votingOption.id, COUNT(v)
            FROM Vote v
            WHERE v.votingProposal = :votingProposal
            GROUP BY v.votingOption
    """)
    List<Object[]> countVotesByOption(@Param("votingProposal") VotingProposal votingProposal);

    boolean existsByVotingProposal(VotingProposal votingProposal);

    boolean existsByVotingProposalAndUser(VotingProposal votingProposal, User user);
}
