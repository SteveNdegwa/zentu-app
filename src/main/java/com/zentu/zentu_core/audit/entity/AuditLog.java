package com.zentu.zentu_core.audit.entity;

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
    private String action;
    private String method;
    private String arguments;
    private String userId;
    private boolean success;
    private String errorMessage;
}
