package com.zentu.zentu_core.controller;

import com.zentu.zentu_core.dto.api.ApiResponse;
import com.zentu.zentu_core.dto.group.CreateGroupRequest;
import com.zentu.zentu_core.dto.group.GroupDto;
import com.zentu.zentu_core.dto.group.UpdateGroupRequest;
import com.zentu.zentu_core.dto.group.UpdateUserGroupRoleRequest;
import com.zentu.zentu_core.dto.user.UserSummaryDto;
import com.zentu.zentu_core.entity.User;
import com.zentu.zentu_core.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createGroup(
            @RequestBody @Valid CreateGroupRequest request, @AuthenticationPrincipal User user) {
        UUID groupId = groupService.createGroup(request, user);
        return ResponseEntity.ok(ApiResponse.success("Group created successfully", groupId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateGroup(
            @PathVariable UUID groupId, @RequestBody @Valid UpdateGroupRequest request, @AuthenticationPrincipal User user) {
        groupService.updateGroup(groupId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable UUID groupId, @AuthenticationPrincipal User user) {
        groupService.deleteGroup(groupId, user);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupDto>> getGroup(@PathVariable UUID groupId) {
        GroupDto groupDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group fetched successfully", groupDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupDto>>> getAllGroups() {
        List<GroupDto> groups = groupService.getAllGroups();
        return ResponseEntity.ok(ApiResponse.success("Groups fetched successfully", groups));
    }

    @PostMapping("/{groupId}/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> addUserToGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
        groupService.addUserToGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User added to the group successfully", null));
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromGroup(
            @PathVariable UUID groupId, @PathVariable UUID userId, @AuthenticationPrincipal User user) {
        groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User removed from the group successfully", null));
    }

    @PostMapping("/{groupId}/admins/{userId}")
    public ResponseEntity<ApiResponse<Void>> setUserAsGroupAdmin(
            @PathVariable UUID groupId, @PathVariable UUID userId, @AuthenticationPrincipal User user) {
        groupService.setUserAsGroupAdmin(groupId, userId, user);
        return ResponseEntity.ok(ApiResponse.success("User set as group admin successfully", null));
    }

    @PatchMapping("/{groupId}/members/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserGroupRole(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @RequestBody UpdateUserGroupRoleRequest request) {
        groupService.updateUserGroupRole(groupId, userId, request.getRole());
        return ResponseEntity.ok(ApiResponse.success("User's role updated successfully", null));
    }

    @GetMapping("/{groupId}/admins")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getGroupAdmins(@PathVariable UUID groupId){
        List<UserSummaryDto> admins = groupService.getGroupAdmins(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group admins fetched successfully", admins));
    }
}
