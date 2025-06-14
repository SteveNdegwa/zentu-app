package com.zentu.zentu_core.user.service;
import com.zentu.zentu_core.user.dto.*;
import org.springframework.http.ResponseEntity;


public interface AppUserService {
    ResponseEntity<?> createAppUser(CreateAppUserDto request);
    ResponseEntity<?> loginUser(LoginRequest request);
    ResponseEntity<?> retrieveProfile(RetrieveProfileRequest request);
}