package com.zentu.zentu_core.billing.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Pledges")
public class Pledge extends BaseEntity {

    @Column(name = "zentu_user", nullable = true)
    private String userPhoneNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", precision = 25, scale = 2, nullable = false)
    private State state = State.PENDING;

    private LocalDateTime fulfilledAt;

}
