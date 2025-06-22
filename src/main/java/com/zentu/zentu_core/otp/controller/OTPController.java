package com.zentu.zentu_core.otp.controller;

import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.otp.dto.SendOtpRequest;
import com.zentu.zentu_core.otp.dto.VerifyOtpRequest;
import com.zentu.zentu_core.otp.service.OTPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/otps")
public class OTPController {

    private final OTPService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendOTP(@RequestBody @Valid SendOtpRequest request) {
        otpService.sendOTP(request);
        return ApiResponse.ok("OTP sent successfully", null);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyOTP(@RequestBody @Valid VerifyOtpRequest request) {
        otpService.verifyOTP(request);
        return ApiResponse.ok("OTP verified successfully", null);
    }
}
