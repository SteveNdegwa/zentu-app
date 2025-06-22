package com.zentu.zentu_core.audit.annotation;

import com.zentu.zentu_core.audit.enums.AuditAction;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    AuditAction action();
}
