package com.zentu.zentu_core.billing.controller;

import com.zentu.zentu_core.billing.service.mpesa.MpesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mpesa")
public class MpesaController {
	
	@Autowired
	private MpesaService mpesaService;
	
	@PostMapping("/callback")
	public ResponseEntity<?> handleCallback(@RequestBody Map<String, Object> payload) {
		return ResponseEntity.ok(mpesaService.processStkCallback(payload));
	}
	
	@PostMapping("/confirmation")
	public ResponseEntity<?> handleConfirmation(@RequestBody Map<String, Object> payload) {
		return ResponseEntity.ok(mpesaService.processConfirmation(payload));
	}
	
	@PostMapping("/validation")
	public ResponseEntity<?> handleValidation(@RequestBody Map<String, Object> payload) {
		return ResponseEntity.ok(mpesaService.processValidation(payload));
	}
	
	@PostMapping("/initiate_stk")
	public ResponseEntity<?> initiateStkPush(@RequestBody Map<String, Object> payload) {
		return ResponseEntity.ok(mpesaService.initiateStkPush(payload));
	}
}

