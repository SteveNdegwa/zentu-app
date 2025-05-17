package com.zentu.zentu_core.billing.entity;


import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.group.entity.Group;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @Column(name = "account_number", nullable = false, unique = true)
    @NotNull(message = "Account number is required")
    private Integer accountNumber;

    @Column(name = "current", nullable = false)
    private BigDecimal current = BigDecimal.ZERO;

    @Column(name = "reserved", nullable = false)
    private BigDecimal reserved = BigDecimal.ZERO;

    @Column(name = "available", nullable = false)
    private BigDecimal available = BigDecimal.ZERO;

    @Column(name = "uncleared", nullable = false)
    private BigDecimal uncleared = BigDecimal.ZERO;

    @Column(name = "charge", nullable = false)
    private BigDecimal charge = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private State state = State.ACTIVE;

}

