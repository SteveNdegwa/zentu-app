package com.zentu.zentu_core.auth.security;

import com.zentu.zentu_core.auth.entity.Identity;
import com.zentu.zentu_core.auth.repository.IdentityRepository;
import com.zentu.zentu_core.base.enums.State;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final IdentityRepository identityRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractTokenFromHeader(request);
        Optional<Identity> identityOpt = identityRepository.findByTokenAndState(token, State.ACTIVE);
        if (identityOpt.isPresent()){
            Identity identity = identityOpt.get();

            LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
            identity.setExpiresAt(expiresAt);
            identityRepository.save(identity);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    identity.getUser(), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}

