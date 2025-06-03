package com.zentu.zentu_core.user.service;
import com.zentu.zentu_core.user.dto.CreateAppUserDto;
import com.zentu.zentu_core.user.dto.LoginRequest;
import org.springframework.http.ResponseEntity;


public interface AppUserService {
    ResponseEntity<?> createAppUser(CreateAppUserDto request);
    ResponseEntity<?> loginUser(LoginRequest request);
}