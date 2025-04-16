package com.zentu.zentu_core.auth.controller;

import com.zentu.zentu_core.auth.dto.LoginRequest;
import com.zentu.zentu_core.auth.service.AuthService;
import com.zentu.zentu_core.base.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody @Valid LoginRequest request){
        Map<String, String> userDetails = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Successfully logged in", userDetails));
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<ApiResponse<Void>> logout(@PathVariable UUID userId){
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success("Successfully logged out", null));
    }
}
