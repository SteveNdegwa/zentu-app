package com.zentu.zentu_core.user.dto;

import lombok.Data;

@Data
public class CreateAppUserDto {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String app;
	private String pin;
}
