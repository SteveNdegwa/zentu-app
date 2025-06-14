package com.zentu.zentu_core.notification.client;

import com.zentu.zentu_core.base.config.IdentityServiceFeignConfig;
import com.zentu.zentu_core.base.dto.JsonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "notificationServiceClient",
        url = "${identity.api-url}",
        configuration = IdentityServiceFeignConfig.class,
        path = "/notifications"
)
public interface NotificationServiceClient {

    @PostMapping("/send/")
    JsonResponse sendNotification(@RequestBody Map<String, Object> data);
}
