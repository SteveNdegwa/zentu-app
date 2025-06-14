package com.zentu.zentu_core.billing.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pesaway_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PesawayTransactionLog extends BaseEntity {
    @Column(name = "alias", nullable = true)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(name = "trx_type", nullable = true)
    @NotNull(message = "Transaction type is required")
    private EntryCategory transactionType = EntryCategory.CREDIT;

    @Column(name = "originator_reference", unique = true, nullable = true)
    private String originatorReference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = true)
    private AccountType accountType;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "receipt")
    private String receipt;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", precision = 25, scale = 2, nullable = false)
    private State state = State.ACTIVE;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

}
