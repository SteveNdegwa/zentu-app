package com.zentu.zentu_core.controller;

import com.zentu.zentu_core.dto.group.CreateGroupRequest;
import com.zentu.zentu_core.dto.group.GroupDto;
import com.zentu.zentu_core.dto.group.UpdateGroupRequest;
import com.zentu.zentu_core.dto.group.UpdateUserGroupRoleRequest;
import com.zentu.zentu_core.dto.user.UserSummaryDto;
import com.zentu.zentu_core.entity.User;
import com.zentu.zentu_core.service.GroupService;
import jakarta.validation.constraints.Min;
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
    public ResponseEntity<GroupDto> createGroup(
            @RequestBody @Valid CreateGroupRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.createGroup(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable UUID id, @RequestBody @Valid UpdateGroupRequest request, @AuthenticationPrincipal User user) {
        GroupDto updatedGroup = groupService.updateGroup(id, request, user);
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        groupService.deleteGroup(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable UUID id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PostMapping("/{groupId}/users/{userId}")
    public ResponseEntity<Void> addUserToGroup(@PathVariable @Min(1) UUID groupId, @PathVariable UUID userId) {
        groupService.addUserToGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromGroup(
            @PathVariable UUID groupId, @PathVariable UUID userId, @AuthenticationPrincipal User user) {
        groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/admins/{userId}")
    public ResponseEntity<Void> setUserAsGroupAdmin(
            @PathVariable UUID groupId, @PathVariable UUID userId, @AuthenticationPrincipal User user) {
        groupService.setUserAsGroupAdmin(groupId, userId, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupId}/members/{userId}/role")
    public ResponseEntity<String> updateUserGroupRole(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @RequestBody UpdateUserGroupRoleRequest request) {
        groupService.updateUserGroupRole(groupId, userId, request.getRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/admins")
    public ResponseEntity<List<UserSummaryDto>> getGroupAdmins(@PathVariable @Min(1) UUID groupId){
        return ResponseEntity.ok(groupService.getGroupAdmins(groupId));
    }
}
