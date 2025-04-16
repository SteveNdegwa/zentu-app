package com.zentu.zentu_core.auth.service;

import com.zentu.zentu_core.auth.dto.LoginRequest;
import com.zentu.zentu_core.auth.entity.Identity;
import com.zentu.zentu_core.auth.repository.IdentityRepository;
import com.zentu.zentu_core.auth.util.TokenGenerator;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IdentityRepository identityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    @Transactional
    public Map<String, String> login (LoginRequest request){
        User user = userRepository.findByPhoneNumberAndState(request.getPhoneNumber(), State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        identityRepository.deactivateUserIdentities(user);

        String token = tokenGenerator.generateToken();

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        identityRepository.save(Identity.builder().token(token).user(user).expiresAt(expiresAt).build());

        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId().toString());
        map.put("token", token);

        return map;
    }

    @Transactional
    public void logout(UUID userId){
        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not found"));
        identityRepository.deactivateUserIdentities(user);
    }

}
