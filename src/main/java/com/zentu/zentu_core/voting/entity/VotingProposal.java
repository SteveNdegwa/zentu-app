package com.zentu.zentu_core.voting.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.voting.enums.VotingProposalState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "voting_proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingProposal extends BaseEntity {
    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User creator;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "votingProposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VotingOption> options = new HashSet<>();

    @OneToMany(mappedBy = "votingProposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private VotingProposalState state = VotingProposalState.DRAFT;

    public void addOption(VotingOption option) {
        option.setVotingProposal(this);
        options.add(option);
    }

    public void removeOption(VotingOption option) {
        options.remove(option);
    }

    public void addVote(Vote vote) {
        vote.setVotingProposal(this);
        votes.add(vote);
    }

    public void removeVote(Vote vote) {
        votes.remove(vote);
    }

}
