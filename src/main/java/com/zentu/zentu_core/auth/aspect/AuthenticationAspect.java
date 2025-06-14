package com.zentu.zentu_core.auth.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthenticationAspect {

    // Intercept methods annotated with @ProtectedEndpoint
    @Around("@annotation(com.zentu.zentu_core.auth.annotation.ProtectedEndpoint)")
    public Object checkAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Invalid or missing token");
        }

        // Proceed with the original method call if authenticated
        return joinPoint.proceed();
    }
}

