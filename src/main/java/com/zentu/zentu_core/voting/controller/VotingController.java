package com.zentu.zentu_core.voting.controller;

import com.zentu.zentu_core.auth.security.annotations.ProtectedEndpoint;
import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.voting.dto.*;
import com.zentu.zentu_core.voting.service.VotingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/voting")
@RequiredArgsConstructor
public class VotingController {

    private final VotingService votingService;

    @PostMapping("/proposals")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<UUID>> createVotingProposal(
            @RequestBody @Valid CreateVotingProposalRequest request,
            @AuthenticationPrincipal User user) {

        UUID proposalId = votingService.createVotingProposal(request, user);
        return ResponseEntity.ok(ApiResponse.success("Proposal created", proposalId));
    }

    @PutMapping("/proposals/{proposalId}/open")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> markProposalAsOpen(
            @PathVariable UUID proposalId,
            @AuthenticationPrincipal User user) {

        votingService.markProposalAsOpen(proposalId, user);
        return ResponseEntity.ok(ApiResponse.success("Proposal marked as open", null));
    }

    @PutMapping("/proposals/{proposalId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> updateVotingProposal(
            @PathVariable UUID proposalId,
            @RequestBody @Valid UpdateVotingProposalRequest request,
            @AuthenticationPrincipal User user) {

        votingService.updateVotingProposal(proposalId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Proposal updated", null));
    }

    @DeleteMapping("/proposals/{proposalId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> deleteVotingProposal(
            @PathVariable UUID proposalId,
            @AuthenticationPrincipal User user) {

        votingService.deleteVotingProposal(proposalId, user);
        return ResponseEntity.ok(ApiResponse.success("Proposal deleted", null));
    }

    @PostMapping("/proposals/{proposalId}/options")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<UUID>> createVotingOption(
            @PathVariable UUID proposalId,
            @RequestBody @Valid CreateVotingOptionRequest request,
            @AuthenticationPrincipal User user) {

        UUID optionId = votingService.createVotingOption(proposalId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Option created", optionId));
    }

    @PutMapping("/options/{optionId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> updateVotingOption(
            @PathVariable UUID optionId,
            @RequestBody @Valid CreateVotingOptionRequest request,
            @AuthenticationPrincipal User user) {

        votingService.updateVotingOption(optionId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Option updated", null));
    }

    @DeleteMapping("/options/{optionId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> deleteVotingOption(
            @PathVariable UUID optionId,
            @AuthenticationPrincipal User user) {

        votingService.deleteVotingOption(optionId, user);
        return ResponseEntity.ok(ApiResponse.success("Option deleted", null));
    }

    @PostMapping("/proposals/{proposalId}/vote/{optionId}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<UUID>> castVote(
            @PathVariable UUID proposalId,
            @PathVariable UUID optionId,
            @AuthenticationPrincipal User user) {

        UUID voteId = votingService.castVote(proposalId, optionId, user);
        return ResponseEntity.ok(ApiResponse.success("Vote cast", voteId));
    }

    @DeleteMapping("/proposals/{proposalId}/vote")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> deleteVote(
            @PathVariable UUID proposalId,
            @AuthenticationPrincipal User user) {

        votingService.deleteVote(proposalId, user);
        return ResponseEntity.ok(ApiResponse.success("Vote removed", null));
    }

    @GetMapping("/proposals/{id}")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<VotingProposalDto>> getProposalWithVotes(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {

        VotingProposalDto dto = votingService.getProposalWithVotes(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched proposal", dto));
    }

    @GetMapping("/proposals/created")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<List<VotingProposalBasicDto>>> getProposalsCreatedByUser(
            @AuthenticationPrincipal User user) {

        List<VotingProposalBasicDto> proposals = votingService.getProposalsCreatedByUser(user);
        return ResponseEntity.ok(ApiResponse.success("Proposals fetched", proposals));
    }

    @GetMapping("/proposals/applicable")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<List<VotingProposalBasicDto>>> getProposalsForUser(
            @AuthenticationPrincipal User user) {

        List<VotingProposalBasicDto> proposals = votingService.getProposalsForUser(user);
        return ResponseEntity.ok(ApiResponse.success("Proposals fetched", proposals));
    }

    @GetMapping("/groups/{groupId}/proposals")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<List<VotingProposalBasicDto>>> getProposalsForGroup(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal User user) {

        List<VotingProposalBasicDto> proposals = votingService.getProposalsForGroup(groupId, user);
        return ResponseEntity.ok(ApiResponse.success("Fetched proposals for group", proposals));
    }
}
