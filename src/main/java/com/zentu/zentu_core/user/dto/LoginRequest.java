package com.zentu.zentu_core.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
	private String username;
	
	private String password;
	
	private String app;
}
