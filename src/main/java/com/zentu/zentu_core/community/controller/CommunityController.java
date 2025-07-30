package com.zentu.zentu_core.community.controller;

import com.zentu.zentu_core.auth.annotation.ProtectedEndpoint;
import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.community.dto.CreateCommunityRequest;
import com.zentu.zentu_core.community.dto.FilterCommunitiesRequest;
import com.zentu.zentu_core.community.dto.UpdateCommunityRequest;
import com.zentu.zentu_core.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> createCommunity(
            @RequestBody @Valid CreateCommunityRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        Map<String, Object> communityData = communityService.createCommunity(request, user);
        return ApiResponse.created("Community created successfully", communityData);
    }

    @PutMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> updateCommunity(
            @PathVariable("id") String communityId,
            @RequestBody @Valid UpdateCommunityRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        communityService.updateCommunity(communityId, request, user);
        return ApiResponse.ok("Community updated successfully", null);
    }

    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> deleteCommunity(
            @PathVariable("id") String communityId,
            @AuthenticationPrincipal Map<String, Object> user) {
        communityService.deleteCommunity(communityId, user);
        return ApiResponse.ok("Community deleted successfully", null);
    }

    @GetMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getCommunity(@PathVariable("id") String communityId) {
        Map<String, Object> community = communityService.getCommunityById(communityId);
        return ApiResponse.ok("Community fetched successfully", Map.of("community", community));
    }

    @GetMapping
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getUserCommunities(@AuthenticationPrincipal Map<String, Object> user) {
        List<Map<String, Object>> communities = communityService.getUserCommunities(user);
        return ApiResponse.ok("User communities fetched successfully", Map.of("communities", communities));
    }

    @PostMapping("/filter")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> filterCommunities(@RequestBody FilterCommunitiesRequest request) {
        List<Map<String, Object>> communities = communityService.filterCommunities(request);
        return ApiResponse.ok("Communities filtered successfully", Map.of("communities", communities));
    }

    @PostMapping("/{id}/members")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> joinCommunity(
            @PathVariable String communityId,
            @AuthenticationPrincipal Map<String, Object> user) {
        communityService.joinCommunity(communityId, user);
        return ApiResponse.ok("User joined the community successfully", null);
    }

    @DeleteMapping("/{id}/members")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> exitCommunity(
            @PathVariable String communityId,
            @AuthenticationPrincipal Map<String, Object> user) {
        communityService.exitCommunity(communityId, user);
        return ApiResponse.ok("User exited the community successfully", null);
    }

    @PostMapping("/{id}/admins/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> setUserAsCommunityAdmin(
            @PathVariable String communityId,
            @PathVariable String userId) {
        communityService.addUserToCommunityAdmins(communityId, userId);
        return ApiResponse.ok("User set as community admin successfully", null);
    }

    @DeleteMapping("/{id}/admins/{userId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> removeUserFromCommunityAdmins(
            @PathVariable String communityId,
            @PathVariable String userId) {
        communityService.removeUserFromCommunityAdmins(communityId, userId);
        return ApiResponse.ok("User removed from community admins successfully", null);
    }

    @PatchMapping("/{id}/members/{userId}/role")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> updateUserCommunityRole(
            @PathVariable String communityId,
            @PathVariable String userId,
            @RequestBody String role) {
        communityService.updateCommunityRole(communityId, userId, role);
        return ApiResponse.ok("User's community role updated successfully", null);
    }

    @GetMapping("/{id}/members")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getCommunityMembers(@PathVariable String communityId) {
        List<Map<String, Object>> members = communityService.getCommunityMembers(communityId);
        return ApiResponse.ok("Community members fetched successfully", Map.of("members", members));
    }

    @PostMapping("/{id}/invite")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> inviteToCommunity(
            @PathVariable String communityId,
            @RequestBody List<String> phoneNumbers){
        communityService.inviteToCommunity(communityId, phoneNumbers);
        return ApiResponse.ok("Invite to community successful", null);
    }
}
