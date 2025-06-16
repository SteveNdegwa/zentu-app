package com.zentu.zentu_core.user.controller;

import com.zentu.zentu_core.common.utils.ResponseProvider;
import com.zentu.zentu_core.user.dto.*;
import com.zentu.zentu_core.user.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppUserController {
	
	private final AppUserService appUserService;
	
	@PostMapping("/register")
	public ResponseEntity<?> createUser(@Valid @RequestBody CreateAppUserDto request) {
		try {
			return appUserService.createAppUser(request);
		} catch (Exception e) {
			return new ResponseProvider("500.001", "Failed to create user account").exception();
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
		try {
			return appUserService.loginUser(request);
		} catch (Exception e) {
			return new ResponseProvider("500.002", "Failed to login user").exception();
		}
	}
	
	@PostMapping("/profile")
	public ResponseEntity<?> retrieveProfile(@Valid @RequestBody RetrieveProfileRequest request) {
		try {
			return appUserService.retrieveProfile(request);
		} catch (Exception e) {
			return new ResponseProvider("500.002", "Failed to login user").exception();
		}
	}
	
	@PostMapping(/"verify/phone")
	public ResponseEntity<?> retrieveProfile(@Valid @RequestBody RetrieveProfileRequest request) {
		try {
			return appUserService.checkPhoneNumber(request);
		} catch (Exception e) {
			return new ResponseProvider("500.002", "Failed to login user").exception();
		}
	}
	
	@PostMapping(/"verify/otp")
	public ResponseEntity<?> retrieveProfile(@Valid @RequestBody RetrieveProfileRequest request) {
		try {
			return appUserService.verifyOtp(request);
		} catch (Exception e) {
			return new ResponseProvider("500.002", "Failed to login user").exception();
		}
	}

}
