package com.zentu.zentu_core.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
	private String phoneNumber;
	
	private String pin;
	
	private String app;
}
