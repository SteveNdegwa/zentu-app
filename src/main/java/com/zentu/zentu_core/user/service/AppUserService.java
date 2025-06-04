package com.zentu.zentu_core.user.service;
import com.zentu.zentu_core.user.dto.*;
import com.zentu.zentu_core.user.dto.VerifyPhoneNumberRequest;
import org.springframework.http.ResponseEntity;


public interface AppUserService {
    ResponseEntity<?> createAppUser(CreateAppUserDto request);
    ResponseEntity<?> loginUser(LoginRequest request);
    ResponseEntity<?> verifyPhoneNumber(VerifyPhoneNumberRequest request);
    ResponseEntity<?> verifyOtp(VerifyOtpRequest request);
    ResponseEntity<?> retrieveProfile(RetrieveProfileRequest request);
}