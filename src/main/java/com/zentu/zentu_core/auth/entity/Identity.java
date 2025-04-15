package com.zentu.zentu_core.auth.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDateTime;

@Entity
@Table(name = "identities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Filter(name = "stateFilter", condition = "state = :state")
public class Identity extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
