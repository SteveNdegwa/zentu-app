package com.zentu.zentu_core.auth.service;

import com.zentu.zentu_core.auth.dto.LoginRequest;
import com.zentu.zentu_core.auth.dto.VerifyUserRequest;
import com.zentu.zentu_core.auth.entity.Identity;
import com.zentu.zentu_core.auth.repository.IdentityRepository;
import com.zentu.zentu_core.auth.util.TokenGenerator;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.base.service.OtpService;
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

    private final OtpService otpService;
    private final IdentityRepository identityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    @Transactional
    private Map<String, String> generateToken(User user) {
        String token = tokenGenerator.generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        State state = user.getIsVerified() ? State.ACTIVE : State.ACTIVATION_PENDING;

        identityRepository.save(
                Identity.builder().token(token).user(user).expiresAt(expiresAt).state(state).build()
        );

        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("tokenState", state.name());

        return map;
    }

    @Transactional
    public Map<String, String> login(LoginRequest request) {
        User user = userRepository.findByPhoneNumberAndState(request.getPhoneNumber(), State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!request.getDeviceId().equals(user.getDeviceId())){
            user.setDeviceId(request.getDeviceId());
            user.setIsVerified(false);
            userRepository.save(user);
        }

        identityRepository.deactivateUserIdentities(user);

        Map<String, String> tokenData = generateToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("userId", user.getId().toString());
        response.putAll(tokenData);

        return response;
    }

    @Transactional
    public void logout(UUID userId){
        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not found"));
        identityRepository.deactivateUserIdentities(user);
    }

    @Transactional
    public void verifyUser(VerifyUserRequest request){
        otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());

        Identity identity = identityRepository.findByTokenAndState(request.getToken(), State.ACTIVATION_PENDING)
                .orElseThrow(()-> new RuntimeException("Invalid token"));

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
        identity.setState(State.ACTIVE);
        identity.setExpiresAt(expiresAt);
        identityRepository.save(identity);

        User user = identity.getUser();
        user.setIsVerified(true);
        userRepository.save(user);
    }
}
