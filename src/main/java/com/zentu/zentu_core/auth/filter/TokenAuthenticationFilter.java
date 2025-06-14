package com.zentu.zentu_core.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractTokenFromHeader(request);

        // TODO: FETCH USER FROM IDMS AND IF AUTHENTICATED SET USER OBJECT
//        Optional<Identity> identityOpt = identityRepository.findByTokenAndState(token, State.ACTIVE);
//        if (identityOpt.isPresent()){
//            Identity identity = identityOpt.get();
//
//            LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
//            identity.setExpiresAt(expiresAt);
//            identityRepository.save(identity);
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    identity.getUser(), null, Collections.emptyList());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
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

