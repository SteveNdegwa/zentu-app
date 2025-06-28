package com.zentu.zentu_core.contribution.service;

import com.zentu.zentu_core.audit.annotation.Auditable;
import com.zentu.zentu_core.audit.enums.AuditAction;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.contribution.dto.CreateContributionRequest;
import com.zentu.zentu_core.contribution.dto.UpdateContributionRequest;
import com.zentu.zentu_core.contribution.entity.Contribution;
import com.zentu.zentu_core.contribution.repository.ContributionRepository;
import com.zentu.zentu_core.community.dto.CreateCommunityRequest;
import com.zentu.zentu_core.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final CommunityService communityService;

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
    @Auditable(action = AuditAction.CREATE_CONTRIBUTION)
    public Map<String, String> createContribution(CreateContributionRequest request, Map<String, Object> user){
        if (request.getCommunityId() == null ||
                request.getCommunityId().isBlank() ||
                "1".equals(request.getCommunityId())){
            if (request.getCommunityName() == null || request.getCommunityName().isBlank()){
                throw new RuntimeException("Community ID or community name must be provided");
            }
            CreateCommunityRequest createCommunityRequest = new CreateCommunityRequest();
            createCommunityRequest.setName(request.getCommunityName().trim());
            Map<String, Object> response = communityService.createCommunity(createCommunityRequest, user);
            request.setCommunityId(response.get("id").toString());
        }
        Contribution contribution = contributionRepository.save(
                Contribution.builder()
                        .name(request.getName())
                        .amount(request.getAmount())
                        .deadline(LocalDate.parse(request.getDeadline()))
                        .alias(generateNextAlias())
                        .communityId(request.getCommunityId())
                        .creatorId(user.get("id").toString())
                        .state(State.ACTIVE)
                        .build()
        );

        if (!request.getPhoneNumbers().isEmpty()){
            try{
                communityService.inviteToCommunity(request.getCommunityId(), request.getPhoneNumbers());
            } catch (Exception e) {
                // Fail silently
                log.error("Invite to community failed for communityId={}, phones={}", request.getCommunityId(),
                        request.getPhoneNumbers(), e);
            }
        }

        return Map.of(
                "communityId", request.getCommunityId(),
                "contributionId", contribution.getId().toString()
        );
    }

    @Transactional
    @Auditable(action = AuditAction.UPDATE_CONTRIBUTION)
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
    @Auditable(action = AuditAction.DELETE_CONTRIBUTION)
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
    public List<Contribution> fetchCommunityContributions(String communityId) {
        return contributionRepository.findAllByCommunityIdAndState(communityId, State.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Contribution> fetchContributionsByCreator(String creatorId) {
        return contributionRepository.findAllByCreatorIdAndState(creatorId, State.ACTIVE);
    }
}
