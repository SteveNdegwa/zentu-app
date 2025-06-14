package com.zentu.zentu_core.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentu.zentu_core.audit.annotation.Auditable;
import com.zentu.zentu_core.audit.entity.AuditLog;
import com.zentu.zentu_core.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("@annotation(auditable)")
    public void auditableMethods(Auditable auditable) {}

    @AfterReturning(pointcut = "auditableMethods(auditable)", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Auditable auditable, Object result) {
        logAudit(joinPoint, auditable, true, null);
    }

    @AfterThrowing(pointcut = "auditableMethods(auditable)", throwing = "ex")
    public void logFailure(JoinPoint joinPoint, Auditable auditable, Throwable ex) {
        logAudit(joinPoint, auditable, false, ex.getMessage());
    }

    private void logAudit(JoinPoint joinPoint, Auditable auditable, boolean success, String errorMessage) {
        AuditLog log = new AuditLog();
        log.setAction(auditable.action());
        log.setMethod(joinPoint.getSignature().toShortString());
        log.setArguments(serialize(joinPoint.getArgs()));
        log.setUserId(getCurrentUser());
        log.setSuccess(success);
        log.setErrorMessage(errorMessage);

        auditLogRepository.save(log);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";
    }

    private String serialize(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            return "ERROR_SERIALIZING_ARGS";
        }
    }

//    @Auditable(action = "CREATE_USER")
}
