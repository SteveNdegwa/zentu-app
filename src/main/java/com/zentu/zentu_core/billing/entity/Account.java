package com.zentu.zentu_core.billing.entity;


import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.user.entity.AppUser;
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
    @JoinColumn(name = "account_group", nullable = true, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group accountGroup;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user", nullable = true, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

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

    public void addUnclearedAmount(BigDecimal amount) {
        this.uncleared = this.uncleared.add(amount);
    }

    public void subtractUnclearedAmount(BigDecimal amount) {
        this.uncleared = this.uncleared.subtract(amount);
    }

    public void addCurrentAmount(BigDecimal amount) {
        this.current = this.current.add(amount);
    }

    public void subtractCurrentAmount(BigDecimal amount) {
        this.current = this.current.subtract(amount);
    }

    public void addAvailableAmount(BigDecimal amount) {
        this.available = this.available.add(amount);
    }

    public void subtractAvailableAmount(BigDecimal amount) {
        this.available = this.available.subtract(amount);
    }

    public void addReservedAmount(BigDecimal amount) {
        this.reserved = this.reserved.add(amount);
    }

    public void subtractReservedAmount(BigDecimal amount) {
        this.reserved = this.reserved.subtract(amount);
    }


}

