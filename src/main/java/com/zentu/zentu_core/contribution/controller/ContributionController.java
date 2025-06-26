package com.zentu.zentu_core.contribution.controller;

import com.zentu.zentu_core.auth.annotation.ProtectedEndpoint;
import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.contribution.dto.CreateContributionRequest;
import com.zentu.zentu_core.contribution.dto.UpdateContributionRequest;
import com.zentu.zentu_core.contribution.entity.Contribution;
import com.zentu.zentu_core.contribution.service.ContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributions")
public class ContributionController {

    private final ContributionService contributionService;

    @PostMapping
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> createContribution(
            @RequestBody @Valid CreateContributionRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        UUID contributionId = contributionService.createContribution(request, user);
        return ApiResponse.created("Contribution created successfully", Map.of("id", contributionId));
    }

    @PutMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> updateContribution(
            @PathVariable("id") UUID contributionId,
            @RequestBody @Valid UpdateContributionRequest request,
            @AuthenticationPrincipal Map<String, Object> user) {
        contributionService.updateContribution(contributionId, request, user);
        return ApiResponse.ok("Contribution updated successfully", null);
    }

    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> deleteContribution(
            @PathVariable("id") UUID contributionId,
            @AuthenticationPrincipal Map<String, Object> user) {
        contributionService.deleteContribution(contributionId, user);
        return ApiResponse.ok("Contribution deleted successfully", null);
    }

    @GetMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getContribution(@PathVariable("id") UUID contributionId) {
        Contribution contribution = contributionService.getContribution(contributionId);
        return ApiResponse.ok("Contribution fetched successfully", Map.of("contribution", contribution));
    }

    @GetMapping("/communities/{communityId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getCommunityContributions(@PathVariable String communityId) {
        List<Contribution> contributions = contributionService.fetchCommunityContributions(communityId);
        return ApiResponse.ok(
                "Community contributions fetched successfully", Map.of("contributions", contributions));
    }

    @GetMapping("/creator")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse> getCreatorContributions(
            @AuthenticationPrincipal Map<String, Object> user) {
        String creatorId = user.get("id").toString();
        List<Contribution> contributions = contributionService.fetchContributionsByCreator(creatorId);
        return ApiResponse.ok(
                "Your contributions fetched successfully", Map.of("contributions", contributions));
    }
}
