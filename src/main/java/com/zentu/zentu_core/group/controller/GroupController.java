package com.zentu.zentu_core.group.controller;

import com.zentu.zentu_core.auth.security.annotations.ProtectedEndpoint;
import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.group.dto.CreateGroupRequest;
import com.zentu.zentu_core.group.dto.GroupDto;
import com.zentu.zentu_core.group.dto.UpdateGroupRequest;
import com.zentu.zentu_core.group.dto.UpdateGroupRoleRequest;
import com.zentu.zentu_core.user.dto.UserSummaryDto;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.group.service.GroupService;
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
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<UUID>> createGroup(
            @RequestBody @Valid CreateGroupRequest request, @AuthenticationPrincipal User user) {
        UUID groupId = groupService.createGroup(request, user);
        return ResponseEntity.ok(ApiResponse.success("Group created successfully", groupId));
    }

    @PutMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> updateGroup(
            @PathVariable UUID groupId,
            @RequestBody @Valid UpdateGroupRequest request,
            @AuthenticationPrincipal User user) {
        groupService.updateGroup(groupId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully", null));
    }

    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable UUID groupId, @AuthenticationPrincipal User user) {
        groupService.deleteGroup(groupId, user);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }

    @GetMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<GroupDto>> getGroup(@PathVariable UUID groupId) {
        GroupDto groupDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group fetched successfully", groupDto));
    }

    @GetMapping
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<List<GroupDto>>> getAllGroups() {
        List<GroupDto> groups = groupService.getAllGroups();
        return ResponseEntity.ok(ApiResponse.success("Groups fetched successfully", groups));
    }

    @PostMapping("/{groupId}/users/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> addUserToGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
        groupService.addUserToGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User added to the group successfully", null));
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> removeUserFromGroup(
            @PathVariable UUID groupId, @PathVariable UUID userId, @AuthenticationPrincipal User user) {
        groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User removed from the group successfully", null));
    }

    @PostMapping("/{groupId}/admins/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> setUserAsGroupAdmin(
            @PathVariable UUID groupId, @PathVariable UUID userId, @AuthenticationPrincipal User user) {
        groupService.setUserAsGroupAdmin(groupId, userId, user);
        return ResponseEntity.ok(ApiResponse.success("User set as group admin successfully", null));
    }

    @PatchMapping("/{groupId}/members/{userId}/role")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> updateUserGroupRole(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @RequestBody UpdateGroupRoleRequest request) {
        groupService.updateUserGroupRole(groupId, userId, request.getRole());
        return ResponseEntity.ok(ApiResponse.success("User's role updated successfully", null));
    }

    @GetMapping("/{groupId}/admins")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getGroupAdmins(@PathVariable UUID groupId){
        List<UserSummaryDto> admins = groupService.getGroupAdmins(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group admins fetched successfully", admins));
    }
}
