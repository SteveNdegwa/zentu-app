package com.zentu.zentu_core.audit.entity;

import com.zentu.zentu_core.audit.enums.AuditAction;
import com.zentu.zentu_core.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    private String method;

    @Column(columnDefinition = "TEXT")
    private String arguments;

    private String userId;

    private boolean success;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
