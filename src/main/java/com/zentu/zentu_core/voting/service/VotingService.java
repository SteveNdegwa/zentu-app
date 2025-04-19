package com.zentu.zentu_core.voting.service;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.group.repository.GroupMembershipRepository;
import com.zentu.zentu_core.group.repository.GroupRepository;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.voting.dto.*;
import com.zentu.zentu_core.voting.entity.Vote;
import com.zentu.zentu_core.voting.entity.VotingOption;
import com.zentu.zentu_core.voting.entity.VotingProposal;
import com.zentu.zentu_core.voting.enums.VotingProposalState;
import com.zentu.zentu_core.voting.repository.VoteRepository;
import com.zentu.zentu_core.voting.repository.VotingOptionRepository;
import com.zentu.zentu_core.voting.repository.VotingProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final VotingProposalRepository votingProposalRepository;
    private final VotingOptionRepository votingOptionRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public UUID createVotingProposal(CreateVotingProposalRequest request, User user) {
        Group group = groupRepository.findByIdAndState(request.getGroupId(), State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getAdmins().contains(user)) {
            throw new RuntimeException("Only group admins can create voting proposals");
        }

        VotingProposal votingProposal = VotingProposal.builder()
                .group(group)
                .name(request.getName())
                .description(request.getDescription())
                .expiresAt(request.getExpiresAt())
                .creator(user)
                .build();

        votingProposalRepository.save(votingProposal);

        return votingProposal.getId();
    }

    @Transactional
    public void markProposalAsOpen(UUID proposalId, User user) {
        VotingProposal proposal = votingProposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("Proposal not found"));

        if (!proposal.getCreator().equals(user)) {
            throw new IllegalArgumentException("You are not the creator of this proposal");
        }

        List<VotingOption> options = votingOptionRepository.findAllByVotingProposal(proposal);
        if (options.size() < 2) {
            throw new IllegalArgumentException("Proposal must have at least two options to be opened");
        }

        proposal.setState(VotingProposalState.OPEN);
        votingProposalRepository.save(proposal);
    }

    @Transactional
    public void updateVotingProposal(UUID proposalId, UpdateVotingProposalRequest request, User user) {
        VotingProposal proposal = votingProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        if (!proposal.getCreator().equals(user)) {
            throw new RuntimeException("Only the creator can update this proposal");
        }

        proposal.setName(request.getName());
        proposal.setDescription(request.getDescription());
        proposal.setExpiresAt(request.getExpiresAt());

        votingProposalRepository.save(proposal);
    }

    @Transactional
    public void deleteVotingProposal(UUID proposalId, User user) {
        VotingProposal proposal = votingProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        if (!proposal.getCreator().equals(user)) {
            throw new RuntimeException("Only the creator can delete this proposal");
        }

        votingProposalRepository.delete(proposal);
    }

    private void checkAndCloseIfExpired(VotingProposal proposal) {
        if (proposal.getExpiresAt().isBefore(LocalDateTime.now())) {
            if (!proposal.getState().equals(VotingProposalState.CLOSED)) {
                proposal.setState(VotingProposalState.CLOSED);
                votingProposalRepository.save(proposal);
            }
            throw new RuntimeException("Proposal has expired and is now closed.");
        }
    }

    private void checkIfExpired(VotingProposal proposal) {
        if (proposal.getExpiresAt().isBefore(LocalDateTime.now())) {
            if (!proposal.getState().equals(VotingProposalState.CLOSED)) {
                proposal.setState(VotingProposalState.CLOSED);
                votingProposalRepository.save(proposal);
            }
        }
    }

    @Transactional
    public UUID createVotingOption(UUID proposalId, CreateVotingOptionRequest request, User user){
        VotingProposal proposal = votingProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        if (!proposal.getCreator().equals(user)) {
            throw new RuntimeException("Only the proposal creator can create a proposal option");
        }

        boolean votesExist = voteRepository.existsByVotingProposal(proposal);
        if (votesExist) {
            throw new RuntimeException("Cannot modify options after a vote has been cast.");
        }

        if (votingOptionRepository.existsByNameAndVotingProposal(request.getName(), proposal)){
            throw new RuntimeException("An option with this name already exists for the proposal");
        }

        VotingOption option = VotingOption.builder().name(request.getName()).build();

        proposal.addOption(option);

        votingProposalRepository.save(proposal);

        return option.getId();
    }

    @Transactional
    public void updateVotingOption(UUID optionId, CreateVotingOptionRequest request, User user) {
        VotingOption option = votingOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        VotingProposal proposal = option.getVotingProposal();

        if (!proposal.getCreator().equals(user)) {
            throw new RuntimeException("Only the proposal creator can update options");
        }

        boolean votesExist = voteRepository.existsByVotingProposal(proposal);
        if (votesExist) {
            throw new RuntimeException("Cannot modify options after a vote has been cast.");
        }

        if (votingOptionRepository.existsByNameAndVotingProposal(request.getName(), proposal)) {
            throw new RuntimeException("An option with this name already exists for the proposal");
        }

        option.setName(request.getName());
        votingOptionRepository.save(option);
    }

    @Transactional
    public void deleteVotingOption(UUID optionId, User user) {
        VotingOption option = votingOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        VotingProposal proposal = option.getVotingProposal();

        if (!proposal.getCreator().equals(user)) {
            throw new RuntimeException("Only the proposal creator can delete options");
        }

        boolean votesExist = voteRepository.existsByVotingProposal(proposal);
        if (votesExist) {
            throw new RuntimeException("Cannot modify options after a vote has been cast.");
        }

        proposal.removeOption(option);
        votingProposalRepository.save(proposal);
    }

    @Transactional
    public UUID castVote(UUID proposalId, UUID optionId, User user) {
        VotingProposal proposal = votingProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        VotingOption option = votingOptionRepository.findByVotingProposalAndId(proposal, optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        if (proposal.getState() == VotingProposalState.DRAFT) {
            throw new IllegalArgumentException("Voting is not allowed on a draft proposal");
        }

        checkAndCloseIfExpired(proposal);

        Vote vote = voteRepository.findByVotingProposalIdAndUserId(proposalId, user.getId())
                .orElse(null);

        if (vote != null) {
            vote.setVotingOption(option); // update existing vote
        } else {
            vote = Vote.builder()
                    .votingProposal(proposal)
                    .votingOption(option)
                    .user(user)
                    .build();
        }

        voteRepository.save(vote);
        return vote.getId();
    }

    @Transactional
    public void deleteVote(UUID proposalId, User user) {
        Vote vote = voteRepository.findByVotingProposalIdAndUserId(proposalId, user.getId())
                .orElseThrow(() -> new RuntimeException("Vote not found"));

        checkAndCloseIfExpired(vote.getVotingProposal());

        voteRepository.delete(vote);
    }

    @Transactional(readOnly = true)
    public VotingProposalDto getProposalWithVotes(UUID proposalId) {
        VotingProposal proposal = votingProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        checkIfExpired(proposal);

        Map<UUID, Long> voteCounts = voteRepository.countVotesByOption(proposal).stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (Long) row[1]
                ));

        List<VotingOptionDto> options = proposal.getOptions().stream()
                .map(option -> VotingOptionDto.builder()
                        .id(option.getId())
                        .name(option.getName())
                        .voteCount(voteCounts.getOrDefault(option.getId(), 0L))
                        .build()
                )
                .toList();

        return VotingProposalDto.builder()
                .id(proposal.getId())
                .name(proposal.getName())
                .description(proposal.getDescription())
                .expiresAt(proposal.getExpiresAt())
                .creatorName(proposal.getCreator().getFirstName() + " " + proposal.getCreator().getLastName())
                .options(options)
                .state(proposal.getState().toString())
                .build();
    }

    private VotingProposalBasicDto buildVotingProposalBasicDto(VotingProposal proposal) {
        checkIfExpired(proposal);
        return VotingProposalBasicDto.builder()
                .id(proposal.getId())
                .name(proposal.getName())
                .description(proposal.getDescription())
                .expiresAt(proposal.getExpiresAt())
                .creatorName(proposal.getCreator().getFirstName() + " " + proposal.getCreator().getLastName())
                .state(proposal.getState().toString())
                .build();
    }

    private List<VotingProposalBasicDto> convertToVotingProposalBasicDtoList(
            List<VotingProposal> proposals, User user){
        return proposals.stream()
                .filter(proposal ->
                        proposal.getState() != VotingProposalState.DRAFT ||
                                proposal.getCreator().equals(user) // Only allow viewing draft if creator
                )
                .map(this::buildVotingProposalBasicDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VotingProposalBasicDto> getProposalsForUser(User user) {
        List<VotingProposal> proposals = votingProposalRepository.findAllByGroupIn(
                groupMembershipRepository.findGroupsByUser(user));

        return convertToVotingProposalBasicDtoList(proposals, user);
    }

    @Transactional(readOnly = true)
    public List<VotingProposalBasicDto> getProposalsCreatedByUser(User user) {
        List<VotingProposal> proposals = votingProposalRepository.findAllByCreator(user);

        return convertToVotingProposalBasicDtoList(proposals, user);
    }

    @Transactional(readOnly = true)
    public List<VotingProposalBasicDto> getProposalsForGroup(UUID groupId, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean isMember = groupMembershipRepository.existsByUserAndGroupAndState(user, group, State.ACTIVE);
        if (!isMember) {
            throw new RuntimeException("You are not a member of this group");
        }

        List<VotingProposal> proposals = votingProposalRepository.findAllByGroup(group);

        return convertToVotingProposalBasicDtoList(proposals, user);
    }

}
