package com.zentu.zentu_core.base.controller;

import com.zentu.zentu_core.auth.security.annotations.ProtectedEndpoint;
import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.base.dto.SendOtpRequest;
import com.zentu.zentu_core.base.dto.VerifyOtpRequest;
import com.zentu.zentu_core.base.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestBody @Valid SendOtpRequest request) {
        otpService.generateOtp(request.getPhoneNumber());
        return ResponseEntity.ok(ApiResponse.success("Otp sent", null));
    }

    @PostMapping("/verify")
    @ProtectedEndpoint
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("Otp verified successfully", null));
    }
}
