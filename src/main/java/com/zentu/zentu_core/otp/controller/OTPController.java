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
@RequestMapping("/otps")
public class OTPController {

    private final OTPService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOTP(@RequestBody @Valid SendOtpRequest request) {
        otpService.sendOTP(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOTP(@RequestBody @Valid VerifyOtpRequest request) {
        otpService.verifyOTP(request);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", null));
    }
}
