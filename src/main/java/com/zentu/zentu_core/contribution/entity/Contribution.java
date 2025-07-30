package com.zentu.zentu_core.contribution.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contributions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contribution extends BaseEntity {
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, unique = true)
    private String alias;

    @Column(nullable = false)
    private String communityId;

    @Column(nullable = false)
    private String creatorId;

    @Column(nullable = false)
    private LocalDate deadline;

    private String whatsappGroupId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
}
