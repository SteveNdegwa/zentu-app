package com.zentu.zentu_core.group.service;

import com.zentu.zentu_core.audit.annotation.Auditable;
import com.zentu.zentu_core.audit.enums.AuditAction;
import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.AccountNumberGenerator;
import com.zentu.zentu_core.group.client.GroupServiceClient;
import com.zentu.zentu_core.group.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor

public class GroupService {

    private final GroupServiceClient groupServiceClient;
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Auditable(action = AuditAction.CREATE_GROUP)
    public Map<String, Object> createGroup(CreateGroupRequest request, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", request.getName());
        data.put("description", request.getDescription());
        data.put("user_id", user.get("id"));
        data.put("app", "zentu");

        JsonResponse response = groupServiceClient.createGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        Map<String, Object> extraFields = response.getExtraFields();
        String groupId = extraFields.get("groupId").toString();
        String alias = extraFields.get("alias").toString();

        Account account = new Account();
        account.setAccountNumber(accountNumberGenerator.generate());
        account.setAccountType(AccountType.GROUP);
        account.setAlias(alias);
        accountRepository.save(account);

        return Map.of("id", groupId, "alias", alias);
    }

    @Auditable(action = AuditAction.UPDATE_GROUP)
    public void updateGroup(String groupId, UpdateGroupRequest request, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("name", request.getName());
        data.put("description", request.getDescription());
        data.put("user_id", user.get("id"));

        JsonResponse response = groupServiceClient.updateGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    @Auditable(action = AuditAction.DELETE_GROUP)
    public void deleteGroup(String groupId, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("user_id", user.get("id"));

        JsonResponse response = groupServiceClient.deleteGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public Object getGroupById(String groupId) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);

        JsonResponse response = groupServiceClient.getGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("group");
    }

    public Object getUserGroups(Map<String, Object> user){
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user.get("id"));
        data.put("app", "zentu");

        JsonResponse response = groupServiceClient.filterGroups(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("groups");
    }

    public Object filterGroups(FilterGroupsRequest request){
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", request.getUserId());
        data.put("creator_id", request.getCreatorId());
        data.put("state", request.getState());
        data.put("search_term", request.getSearchTerm());

        JsonResponse response = groupServiceClient.filterGroups(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("groups");
    }

    public void joinGroup(String groupId, String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("user_id", userId);

        JsonResponse response = groupServiceClient.joinGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void exitGroup(String groupId, String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("user_id", userId);

        JsonResponse response = groupServiceClient.exitGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void addUserToGroupAdmins(String groupId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("user_id", userId);

        JsonResponse response = groupServiceClient.addUserToGroupAdmins(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void removeUserFromGroupAdmins(String groupId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("user_id", userId);

        JsonResponse response = groupServiceClient.removeUserFromGroupAdmins(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void updateGroupRole(String groupId, String userId, String role) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("user_id", userId);
        data.put("role", role);

        JsonResponse response = groupServiceClient.updateGroupRole(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public Object getGroupMembers(String groupId) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);

        JsonResponse response = groupServiceClient.getGroupMembers(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return response.getExtraFields().get("members");
    }
}
