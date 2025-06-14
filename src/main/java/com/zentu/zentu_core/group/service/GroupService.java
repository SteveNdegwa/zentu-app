package com.zentu.zentu_core.group.service;

import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.group.client.GroupServiceClient;
import com.zentu.zentu_core.group.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor

public class GroupService {

    private final GroupServiceClient groupServiceClient;

    public Map<String, Object> createGroup(CreateGroupRequest request, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", request.getName());
        data.put("description", request.getDescription());
        data.put("user_id", user.get("id"));
        data.put("system", "zentu");

        JsonResponse response = groupServiceClient.createGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return Map.of("groupId", response.getExtraFields().get("group_id"));
    }

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

    public void deleteGroup(String groupId, Map<String, Object> user) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);
        data.put("user_id", user.get("id"));

        JsonResponse response = groupServiceClient.deleteGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public Map<String, Object> getGroupById(String groupId) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);

        JsonResponse response = groupServiceClient.getGroup(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return Map.of("group", response.getExtraFields().get("group"));
    }

    public Map<String, Object> filterGroups(FilterGroupsRequest request){
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", request.getUserId());
        data.put("creator_id", request.getCreatorId());
        data.put("state", request.getState());
        data.put("search_term", request.getSearchTerm());

        JsonResponse response = groupServiceClient.filterGroups(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return Map.of("groups", response.getExtraFields().get("groups"));
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

    public Map<String, Object> getGroupMembers(String groupId) {
        Map<String, Object> data = new HashMap<>();
        data.put("group_id", groupId);

        JsonResponse response = groupServiceClient.getGroupMembers(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }

        return Map.of("members", response.getExtraFields().get("members"));
    }
}
