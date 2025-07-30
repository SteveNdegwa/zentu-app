package com.zentu.zentu_core.community.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zentu.zentu_core.audit.annotation.Auditable;
import com.zentu.zentu_core.audit.enums.AuditAction;
import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.AccountNumberGenerator;
import com.zentu.zentu_core.common.utils.CommonUtils;
import com.zentu.zentu_core.common.utils.PhoneUtils;
import com.zentu.zentu_core.community.client.CommunityServiceClient;
import com.zentu.zentu_core.community.dto.*;
import com.zentu.zentu_core.contribution.client.WassengerApiClient;
import com.zentu.zentu_core.contribution.entity.Contribution;
import com.zentu.zentu_core.contribution.repository.ContributionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor

public class CommunityService {

    private final CommunityServiceClient communityServiceClient;
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final ContributionRepository contributionRepository;
    private final WassengerApiClient wassengerApiClient;

    @Auditable(action = AuditAction.CREATE_COMMUNITY)
    public Map<String, Object> createCommunity(CreateCommunityRequest request, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", request.getName());
        data.put("description", request.getDescription());
        data.put("phone_numbers", request.getPhoneNumbers());
        data.put("user_id", user.get("id"));
        data.put("app", "ZENTU_APP");

        JsonResponse response = communityServiceClient.createCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        String communityId = response.getAdditionalData("community_id", new TypeReference<String>() {});
        String alias = response.getAdditionalData("alias", new TypeReference<String>() {});

        Account account = new Account();
        account.setAccountNumber(accountNumberGenerator.generate());
        account.setAccountType(AccountType.COMMUNITY);
        account.setAlias(alias);
        accountRepository.save(account);

        return Map.of("id", communityId, "alias", alias);
    }

    @Auditable(action = AuditAction.UPDATE_COMMUNITY)
    public void updateCommunity(String communityId, UpdateCommunityRequest request, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("name", request.getName());
        data.put("description", request.getDescription());
        data.put("user_id", user.get("id"));

        JsonResponse response = communityServiceClient.updateCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    @Auditable(action = AuditAction.DELETE_COMMUNITY)
    public void deleteCommunity(String communityId, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", user.get("id"));

        JsonResponse response = communityServiceClient.deleteCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public Map<String, Object> getCommunityById(String communityId) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);

        JsonResponse response = communityServiceClient.getCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getAdditionalData("community", new TypeReference<Map<String, Object>>() {});
    }

    public List<Map<String, Object>> getUserCommunities(Map<String, Object> user){
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user.get("id"));
        data.put("app", "ZENTU_APP");
        JsonResponse response = communityServiceClient.filterCommunities(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getAdditionalData("communities", new TypeReference<List<Map<String, Object>>>() {});
    }

    public List<Map<String, Object>> filterCommunities(FilterCommunitiesRequest request){
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", request.getUserId());
        data.put("creator_id", request.getCreatorId());
        data.put("state", request.getState());
        data.put("search_term", request.getSearchTerm());

        JsonResponse response = communityServiceClient.filterCommunities(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getAdditionalData("communities", new TypeReference<List<Map<String, Object>>>() {});
    }

    public void joinCommunity(String communityId, Map<String, Object> user){
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", user.get("id"));

        JsonResponse response = communityServiceClient.joinCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        // Add the user to the communities' whatsapp groups
        try{
            String phoneNumber = user.get("phone_number").toString();
            String normalized = "+" + PhoneUtils.normalizePhoneNumber(phoneNumber, "254", 12);
            wassengerApiClient.checkNumberExists(Map.of("phone", normalized));

            List<Contribution> contributions = contributionRepository.findAllByCommunityIdAndState(
                    communityId, State.ACTIVE);

            for (Contribution contribution : contributions) {
                String whatsappGroupId = contribution.getWhatsappGroupId();
                if (whatsappGroupId == null || whatsappGroupId.isBlank()) {
                    continue;
                }

                List<Map<String, Object>> participants = new ArrayList<>();
                participants.add(Map.of("phone", normalized, "admin", false));

                wassengerApiClient.addParticipantsToGroup(whatsappGroupId, Map.of("participants", participants));
            }
        } catch (FeignException.BadRequest ignored) {}

    }

    public void exitCommunity(String communityId, Map<String, Object> user){
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", user.get("id"));

        JsonResponse response = communityServiceClient.exitCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void addUserToCommunityAdmins(String communityId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", userId);

        JsonResponse response = communityServiceClient.addUserToCommunityAdmins(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void removeUserFromCommunityAdmins(String communityId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", userId);

        JsonResponse response = communityServiceClient.removeUserFromCommunityAdmins(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void updateCommunityRole(String communityId, String userId, String role) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", userId);
        data.put("role", role);

        JsonResponse response = communityServiceClient.updateCommunityRole(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public List<Map<String, Object>> getCommunityMembers(String communityId) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);

        JsonResponse response = communityServiceClient.getCommunityMembers(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getAdditionalData("members", new TypeReference<List<Map<String, Object>>>() {});
    }

    public void inviteToCommunity(String communityId, List<String> phoneNumbers){
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("phone_numbers", phoneNumbers);

        JsonResponse response = communityServiceClient.inviteToCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        // Invite to community contributions' whatsapp groups
        List<String> validPhoneNumbers = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            String normalized = "+" + PhoneUtils.normalizePhoneNumber(phoneNumber, "254", 12);
            try{
                wassengerApiClient.checkNumberExists(Map.of("phone", normalized));
                validPhoneNumbers.add(normalized);
            } catch (FeignException.BadRequest ignored) {}
        }

        if (validPhoneNumbers.isEmpty()){
            return;
        }

        List<Contribution> contributions = contributionRepository.findAllByCommunityIdAndState(
                communityId, State.ACTIVE);
        for (Contribution contribution : contributions){
            String whatsappGroupId = contribution.getWhatsappGroupId();
            if (whatsappGroupId == null || whatsappGroupId.isBlank()){
                continue;
            }
            try {
                Map<String, Object> groupInviteCode = wassengerApiClient.getGroupInviteCode(whatsappGroupId);

                List<List<String>> phoneChunks = CommonUtils.chunkList(validPhoneNumbers, 10);
                for (List<String> chunk : phoneChunks) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("contacts", chunk);
                    payload.put("message", String.format(
                            "You’ve been invited to contribute to **%s**.\nTap below to view and join:\n%s",
                            contribution.getName(), groupInviteCode.get("url")
                    ));

                    wassengerApiClient.sendMessage(payload);
                }

            } catch (Exception ignored) {}
        }
    }
}
