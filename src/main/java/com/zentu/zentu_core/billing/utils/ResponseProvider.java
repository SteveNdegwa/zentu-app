package com.zentu.zentu_core.billing.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseProvider {
	
	private final Map<String, Object> data = new HashMap<>();
	
	public ResponseProvider() {}
	
	public ResponseProvider(String code, String message) {
		this.data.put("code", code);
		this.data.put("message", message);
	}
	
	public ResponseProvider(Map<String, Object> data) {
		this.data.putAll(data);
	}
	
	private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status) {
		return new ResponseEntity<>(data, status);
	}
	
	public ResponseEntity<Map<String, Object>> success() {
		return buildResponse(HttpStatus.OK);
	}
	
	public ResponseEntity<Map<String, Object>> badRequest() {
		return buildResponse(HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<Map<String, Object>> unauthorized() {
		return buildResponse(HttpStatus.UNAUTHORIZED);
	}
	
	public ResponseEntity<Map<String, Object>> exception() {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
