package com.zentu.zentu_core.contribution.service;

import com.zentu.zentu_core.audit.annotation.Auditable;
import com.zentu.zentu_core.audit.enums.AuditAction;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.AccountNumberGenerator;
import com.zentu.zentu_core.common.utils.PhoneUtils;
import com.zentu.zentu_core.contribution.client.WassengerApiClient;
import com.zentu.zentu_core.contribution.dto.CreateContributionRequest;
import com.zentu.zentu_core.contribution.dto.UpdateContributionRequest;
import com.zentu.zentu_core.contribution.entity.Contribution;
import com.zentu.zentu_core.contribution.repository.ContributionRepository;
import com.zentu.zentu_core.community.dto.CreateCommunityRequest;
import com.zentu.zentu_core.community.service.CommunityService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CommunityService communityService;
    private final WassengerApiClient wassengerApiClient;

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

            // Create a new community if none is provided
            CreateCommunityRequest createCommunityRequest = new CreateCommunityRequest();
            createCommunityRequest.setName(request.getCommunityName().trim());
            createCommunityRequest.setPhoneNumbers(request.getPhoneNumbers());
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

        // Create contribution wallet
        Account account = new Account();
        account.setAccountNumber(accountNumberGenerator.generate());
        account.setAccountType(AccountType.CONTRIBUTION);
        account.setAlias(contribution.getAlias());
        accountRepository.save(account);

        // Optionally create whatsapp group
        if (request.isCreateWhatsappGroup()){
            String whatsappGroupId = createWhatsappGroup(contribution.getId(), user);
            contribution.setWhatsappGroupId(whatsappGroupId);
            contributionRepository.save(contribution);
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

    @Transactional(readOnly = true)
    public String createWhatsappGroup(UUID contributionId, Map<String, Object> user) {
        Contribution contribution = contributionRepository.findByIdAndState(contributionId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        if (!contribution.getCreatorId().equals(user.get("id"))) {
            throw new RuntimeException("Not authorized to create this group");
        }

        if (!contribution.getWhatsappGroupId().isBlank()) {
            return contribution.getWhatsappGroupId();
        }

        List<Map<String, Object>> participants = new ArrayList<>();

        List<Map<String, Object>> members = communityService.getCommunityMembers(contribution.getCommunityId());
        for (Map<String, Object> member : members){
            String phoneNumber = member.get("phone_number").toString();
            if (phoneNumber == null || phoneNumber.isBlank()) {
                continue;
            }
            try {
                String normalized = "+" + PhoneUtils.normalizePhoneNumber(phoneNumber, "254", 12);
                wassengerApiClient.checkNumberExists(Map.of("phone", normalized));
                if (phoneNumber.equals(user.get("phone_number"))) {
                    participants.add(Map.of("phone", normalized, "admin", true));
                }
                else {
                    participants.add(Map.of("phone", normalized, "admin", false));
                }
            } catch (FeignException.BadRequest ignored) {}
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", contribution.getName());
        payload.put("participants", participants);

        Map<String, Object> groupInfo = wassengerApiClient.createGroup(payload);

        return groupInfo.get("wid").toString();
    }
}
