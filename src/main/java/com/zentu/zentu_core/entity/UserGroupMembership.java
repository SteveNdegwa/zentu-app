package com.zentu.zentu_core.entity;

import com.zentu.zentu_core.enums.GroupRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_group_membership", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupMembership extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupRole role = GroupRole.MEMBER;

}
