package com.zentu.zentu_core.billing.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "pesaway_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PesawayTransactionLog extends BaseEntity {
    @Column(name = "alias", nullable = true)
    private String groupAlias;

    @Column(name = "zentu_user", nullable = true)
    private String userId;

    @Column(name = "originator_reference", unique = true, nullable = false)
    private String originatorReference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "receipt")
    private String receipt;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", precision = 25, scale = 2, nullable = false)
    private State state = State.ACTIVE;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

}
