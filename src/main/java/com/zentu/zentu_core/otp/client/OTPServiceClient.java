package com.zentu.zentu_core.otp.client;

import com.zentu.zentu_core.base.config.IdentityServiceFeignConfig;
import com.zentu.zentu_core.base.dto.JsonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(
        name = "otpServiceClient",
        url = "${identity.api-url}",
        configuration = IdentityServiceFeignConfig.class
)
@RequestMapping("/otps")
public interface OTPServiceClient {
    @PostMapping("/send/")
    JsonResponse sendOTP(@RequestBody Map<String, Object> data);

    @PostMapping("/verify/")
    JsonResponse verifyOTP(@RequestBody Map<String, Object> data);
}
