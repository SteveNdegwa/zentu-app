package com.zentu.zentu_core.contribution.service;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.contribution.dto.CreateContributionRequest;
import com.zentu.zentu_core.contribution.dto.UpdateContributionRequest;
import com.zentu.zentu_core.contribution.entity.Contribution;
import com.zentu.zentu_core.contribution.repository.ContributionRepository;
import com.zentu.zentu_core.group.dto.CreateGroupRequest;
import com.zentu.zentu_core.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final GroupService groupService;

    @Transactional
    private String generateNextAlias() {
        Optional<Contribution> latest = contributionRepository.findTopByOrderByAliasDesc();

        long nextNumber = 1;

        if (latest.isPresent()) {
            String lastAlias = latest.get().getAlias();
            try {
                String numberPart = lastAlias.replace("C-", "");
                nextNumber = Long.parseLong(numberPart) + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid alias format: " + lastAlias);
            }
        }

        int width = Math.max(4, String.valueOf(nextNumber).length());
        return String.format("C-%0" + width + "d", nextNumber);
    }

    @Transactional
    public UUID createContribution(CreateContributionRequest request, Map<String, Object> user){
        if (request.getGroupId() == null || request.getGroupId().isBlank()){
            if (request.getGroupName() == null || request.getGroupName().isBlank()){
                throw new RuntimeException("Group ID or group name must be provided");
            }
            CreateGroupRequest createGroupRequest = new CreateGroupRequest();
            createGroupRequest.setName(request.getName().trim());
            Map<String, Object> response = groupService.createGroup(createGroupRequest, user);
            request.setGroupId(response.get("id").toString());
        }
        Contribution contribution = contributionRepository.save(
                Contribution.builder()
                        .name(request.getName())
                        .amount(request.getAmount())
                        .alias(generateNextAlias())
                        .groupId(request.getGroupId())
                        .creatorId(user.get("id").toString())
                        .build()
        );

        return contribution.getId();
    }

    @Transactional
    public void updateContribution(UUID contributionId, UpdateContributionRequest request, Map<String, Object> user) {
        Contribution contribution = contributionRepository.findByIdAndState(contributionId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        if (!contribution.getCreatorId().equals(user.get("id").toString())) {
            throw new RuntimeException("You are not allowed to update this contribution.");
        }

        contribution.setName(request.getName().trim());
        contribution.setAmount(request.getAmount());

        contributionRepository.save(contribution);
    }

    @Transactional
    public void deleteContribution(UUID contributionId, Map<String, Object> user) {
        Contribution contribution = contributionRepository.findByIdAndState(contributionId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        if (!contribution.getCreatorId().equals(user.get("id").toString())) {
            throw new RuntimeException("Not authorized to delete this contribution");
        }

        contribution.setState(State.DELETED);
        contributionRepository.save(contribution);
    }

    @Transactional(readOnly = true)
    public Contribution getContribution(UUID contributionId) {
        return contributionRepository.findByIdAndState(contributionId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Contribution not found or deleted"));
    }

    @Transactional(readOnly = true)
    public List<Contribution> fetchGroupContributions(String groupId) {
        return contributionRepository.findAllByGroupIdAndState(groupId, State.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Contribution> fetchContributionsByCreator(String creatorId) {
        return contributionRepository.findAllByCreatorIdAndState(creatorId, State.ACTIVE);
    }

}
