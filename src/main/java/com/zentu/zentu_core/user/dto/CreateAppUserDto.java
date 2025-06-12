package com.zentu.zentu_core.user.dto;

import lombok.Data;

@Data
public class CreateAppUserDto {
	private String role;
	private String firstName;
	private String lastName;
	private String otherName;
	private String phoneNumber;
	private String email;
	private String app;
	private boolean setRandomPin;
	private String newPin;
	private String confirmPin;
	private boolean changePinNextLogin;
}
