package com.zentu.zentu_core.otp.service;

import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.otp.client.OTPServiceClient;
import com.zentu.zentu_core.otp.dto.SendOtpRequest;
import com.zentu.zentu_core.otp.dto.VerifyOtpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final OTPServiceClient otpServiceClient;

    public void sendOTP(SendOtpRequest request){
        Map<String, Object> data = new HashMap<>();
        data.put("purpose", request.getPurpose());
        data.put("user_id", request.getUserId());
        data.put("contact", request.getContact());
        data.put("delivery_method", request.getDeliveryMethod());

        JsonResponse response = otpServiceClient.sendOTP(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }

    public void verifyOTP(VerifyOtpRequest request){
        Map<String, Object> data = new HashMap<>();
        data.put("purpose", request.getPurpose());
        data.put("user_id", request.getUserId());
        data.put("contact", request.getContact());
        data.put("code", request.getCode());

        JsonResponse response = otpServiceClient.verifyOTP(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }
}
