package com.zentu.zentu_core.user.dto;

import lombok.Data;

@Data
public class VerifyOtp {
	private String phoneNumber;
	private String otp;
}
