package com.zentu.zentu_core.voting.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "votes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "voting_proposal_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "voting_proposal_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private VotingProposal votingProposal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voting_option_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private VotingOption votingOption;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
