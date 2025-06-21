package com.zentu.zentu_core.group.controller;

import com.zentu.zentu_core.auth.annotation.ProtectedEndpoint;
import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.group.dto.CreateGroupRequest;
import com.zentu.zentu_core.group.dto.UpdateGroupRequest;
import com.zentu.zentu_core.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Map<String, Object>>> createGroup(
            @RequestBody @Valid CreateGroupRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        Map<String, Object> groupId = groupService.createGroup(request, user);
        return ResponseEntity.ok(ApiResponse.success("Group created successfully", groupId));
    }

    @PutMapping("/{id}")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> updateGroup(
            @PathVariable("id") String groupId,
            @RequestBody @Valid UpdateGroupRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        groupService.updateGroup(groupId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully", null));
    }

    @DeleteMapping("/{id}")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable("id") String groupId,
            @AuthenticationPrincipal Map<String, Object> user) {
        groupService.deleteGroup(groupId, user);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }

    @GetMapping("/{id}")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGroup(@PathVariable("id") String groupId) {
        Map<String, Object> group = groupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group fetched successfully", group));
    }

    @PostMapping("/{groupId}/users/{userId}")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> addUserToGroup(
            @PathVariable String groupId,
            @PathVariable String userId) {
        groupService.joinGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User added to the group successfully", null));
    }

    @DeleteMapping("/{groupId}/users/{userId}")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> removeUserFromGroup(
            @PathVariable String groupId,
            @PathVariable String userId,
            @AuthenticationPrincipal Map<String, Object> user) {
        groupService.exitGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User removed from the group successfully", null));
    }

    @PostMapping("/{groupId}/admins/{userId}")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> setUserAsGroupAdmin(
            @PathVariable String groupId,
            @PathVariable String userId) {
        groupService.addUserToGroupAdmins(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User set as group admin successfully", null));
    }

    @DeleteMapping("/{groupId}/admins/{userId}")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> removeUserFromGroupAdmins(
            @PathVariable String groupId,
            @PathVariable String userId) {
        groupService.removeUserFromGroupAdmins(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("User removed from group admins successfully", null));
    }

    @PatchMapping("/{groupId}/members/{userId}/role")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> updateUserGroupRole(
            @PathVariable String groupId,
            @PathVariable String userId,
            @RequestBody String role) {
        groupService.updateGroupRole(groupId, userId, role);
        return ResponseEntity.ok(ApiResponse.success("User's role updated successfully", null));
    }

    @GetMapping("/{groupId}/members")
//    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGroupMembers(@PathVariable String groupId) {
        Map<String, Object> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group members fetched successfully", members));
    }
}
