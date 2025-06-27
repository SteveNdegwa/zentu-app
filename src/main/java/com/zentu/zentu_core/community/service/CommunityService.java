package com.zentu.zentu_core.community.service;

import com.zentu.zentu_core.audit.annotation.Auditable;
import com.zentu.zentu_core.audit.enums.AuditAction;
import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.AccountNumberGenerator;
import com.zentu.zentu_core.community.client.CommunityServiceClient;
import com.zentu.zentu_core.community.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor

public class CommunityService {

    private final CommunityServiceClient communityServiceClient;
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Auditable(action = AuditAction.CREATE_COMMUNITY)
    public Map<String, Object> createCommunity(CreateCommunityRequest request, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", request.getName());
        data.put("description", request.getDescription());
        data.put("user_id", user.get("id"));
        data.put("app", "ZENTU_APP");

        JsonResponse response = communityServiceClient.createCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        Map<String, Object> extraFields = response.getExtraFields();
        String communityId = extraFields.get("communityId").toString();
        String alias = extraFields.get("alias").toString();

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

    public Object getCommunityById(String communityId) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);

        JsonResponse response = communityServiceClient.getCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("community");
    }

    public Object getUserCommunities(Map<String, Object> user){
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user.get("id"));
        data.put("app", "ZENTU_APP");
        JsonResponse response = communityServiceClient.filterCommunities(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("communities");
    }

    public Object filterCommunities(FilterCommunitiesRequest request){
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", request.getUserId());
        data.put("creator_id", request.getCreatorId());
        data.put("state", request.getState());
        data.put("search_term", request.getSearchTerm());

        JsonResponse response = communityServiceClient.filterCommunities(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("communities");
    }

    public void joinCommunity(String communityId, String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", userId);

        JsonResponse response = communityServiceClient.joinCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void exitCommunity(String communityId, String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("user_id", userId);

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

    public Object getCommunityMembers(String communityId) {
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);

        JsonResponse response = communityServiceClient.getCommunityMembers(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("members");
    }

    public void inviteToCommunity(String communityId, List<String> phoneNumbers){
        Map<String, Object> data = new HashMap<>();
        data.put("community_id", communityId);
        data.put("phoneNumbers", phoneNumbers);

        JsonResponse response = communityServiceClient.inviteToCommunity(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }
}
