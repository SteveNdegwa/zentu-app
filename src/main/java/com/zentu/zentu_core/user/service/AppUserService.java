package com.zentu.zentu_core.user.service;

import com.zentu.zentu_core.user.entity.AppUser;
import com.zentu.zentu_core.user.entity.UserSession;
import com.zentu.zentu_core.user.repository.AppUserRepository;
import com.zentu.zentu_core.user.repository.UserSessionRepository;
import com.zentu.zentu_core.user.service.util.UserServiceSync;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final UserServiceSync userServiceSync;

    public AppUser createUser(Map<String, Object> userData) {
        Map<String, Object> response = userServiceSync.sync("create", userData);
        if ("success".equals(response.get("status"))) {
            AppUser user = new AppUser();
            user.setUserId((String) response.get("user_id"));
            user.setMetadata((Map<String, Object>) response.get("metadata"));
            return userRepository.save(user);
        } else {
            throw new RuntimeException("Failed to create user: " + response.get("message"));
        }
    }

    public void loginUser(String userId) {
        AppUser user = userRepository.findByUserId(userId).orElseThrow();
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        sessionRepository.save(new UserSession(null, user, "login", null));
        userServiceSync.sync("login", Map.of("user_id", userId));
    }

    public void logoutUser(String userId) {
        AppUser user = userRepository.findByUserId(userId).orElseThrow();
        sessionRepository.save(new UserSession(null, user, "logout", null));
        userServiceSync.sync("logout", Map.of("user_id", userId));
    }

    public void disableUser(String userId) {
        AppUser user = userRepository.findByUserId(userId).orElseThrow();
        user.setIsActive(false);
        userRepository.save(user);
        userServiceSync.sync("disable", Map.of("user_id", userId));
    }
}
