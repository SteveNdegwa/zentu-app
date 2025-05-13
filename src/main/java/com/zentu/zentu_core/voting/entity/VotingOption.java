package com.zentu.zentu_core.voting.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "voting_options", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "voting_proposal_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingOption extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voting_proposal_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private VotingProposal votingProposal;

}
