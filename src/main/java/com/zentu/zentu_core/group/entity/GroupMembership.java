package com.zentu.zentu_core.group.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.group.enums.GroupRole;
import com.zentu.zentu_core.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "group_membership", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Filter(name = "stateFilter", condition = "state = :state")
public class GroupMembership extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupRole role = GroupRole.MEMBER;

}
