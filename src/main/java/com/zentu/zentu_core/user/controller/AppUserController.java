package com.zentu.zentu_core.user.controller;

import com.zentu.zentu_core.common.utils.ResponseProvider;
import com.zentu.zentu_core.user.dto.CreateAppUserDto;
import com.zentu.zentu_core.user.dto.LoginRequest;
import com.zentu.zentu_core.user.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
}
