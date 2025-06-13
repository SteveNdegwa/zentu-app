package com.zentu.zentu_core.service.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bids")
@Getter
@Setter
public class Bid extends BaseEntity {

    @Column(name = "bid_amount", nullable = false)
    @NotNull(message = "Bid amount is required")
    private BigDecimal bidAmount;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "preferred_date")
    private LocalDate preferredDate;

    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service", nullable = false)
    private Service service;

    @Column(name="bidder", nullable = false)
    private String bidder;

    @Column(name = "accepted", nullable = false)
    private boolean accepted = false;
}

