package com.zentu.zentu_core.group.controller;

import com.zentu.zentu_core.audit.annotation.Auditable;
import com.zentu.zentu_core.audit.enums.AuditAction;
import com.zentu.zentu_core.auth.annotation.ProtectedEndpoint;
import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.group.dto.CreateGroupRequest;
import com.zentu.zentu_core.group.dto.FilterGroupsRequest;
import com.zentu.zentu_core.group.dto.UpdateGroupRequest;
import com.zentu.zentu_core.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@Slf4j
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @ProtectedEndpoint
    @Auditable(action = AuditAction.CREATE_GROUP)
    public ResponseEntity<ApiResponse> createGroup(
            @RequestBody @Valid CreateGroupRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        log.error("create group ---- User: {}", user);
        Map<String, Object> groupData = groupService.createGroup(request, user);
        return ApiResponse.created("Group created successfully", groupData);
    }

    @PutMapping("/{id}")
    @ProtectedEndpoint
    @Auditable(action = AuditAction.UPDATE_GROUP)
    public ResponseEntity<ApiResponse> updateGroup(
            @PathVariable("id") String groupId,
            @RequestBody @Valid UpdateGroupRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        groupService.updateGroup(groupId, request, user);
        return ApiResponse.ok("Group updated successfully", null);
    }

    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    @Auditable(action = AuditAction.DELETE_GROUP)
    public ResponseEntity<ApiResponse> deleteGroup(
            @PathVariable("id") String groupId,
            @AuthenticationPrincipal Map<String, Object> user) {
        groupService.deleteGroup(groupId, user);
        return ApiResponse.ok("Group deleted successfully", null);
    }

    @GetMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getGroup(@PathVariable("id") String groupId) {
        Object group = groupService.getGroupById(groupId);
        return ApiResponse.ok("Group fetched successfully", Map.of("group", group));
    }

    @GetMapping
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getUserGroups(@AuthenticationPrincipal Map<String, Object> user) {
        Object groups = groupService.getUserGroups(user);
        return ApiResponse.ok("User groups fetched successfully", Map.of("groups", groups));
    }

    @PostMapping("/filter")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> filterGroups(@RequestBody FilterGroupsRequest request) {
        Object groups = groupService.filterGroups(request);
        return ApiResponse.ok("Groups filtered successfully", Map.of("groups", groups));
    }

    @PostMapping("/{groupId}/users/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> addUserToGroup(
            @PathVariable String groupId,
            @PathVariable String userId) {
        groupService.joinGroup(groupId, userId);
        return ApiResponse.ok("User added to the group successfully", null);
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> removeUserFromGroup(
            @PathVariable String groupId,
            @PathVariable String userId,
            @AuthenticationPrincipal Map<String, Object> user) {
        groupService.exitGroup(groupId, userId);
        return ApiResponse.ok("User removed from the group successfully", null);
    }

    @PostMapping("/{groupId}/admins/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> setUserAsGroupAdmin(
            @PathVariable String groupId,
            @PathVariable String userId) {
        groupService.addUserToGroupAdmins(groupId, userId);
        return ApiResponse.ok("User set as group admin successfully", null);
    }

    @DeleteMapping("/{groupId}/admins/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> removeUserFromGroupAdmins(
            @PathVariable String groupId,
            @PathVariable String userId) {
        groupService.removeUserFromGroupAdmins(groupId, userId);
        return ApiResponse.ok("User removed from group admins successfully", null);
    }

    @PatchMapping("/{groupId}/members/{userId}/role")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> updateUserGroupRole(
            @PathVariable String groupId,
            @PathVariable String userId,
            @RequestBody String role) {
        groupService.updateGroupRole(groupId, userId, role);
        return ApiResponse.ok("User's role updated successfully", null);
    }

    @GetMapping("/{groupId}/members")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getGroupMembers(@PathVariable String groupId) {
        Object members = groupService.getGroupMembers(groupId);
        return ApiResponse.ok("Group members fetched successfully", Map.of("members", members));
    }
}
