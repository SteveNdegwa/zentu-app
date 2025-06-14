package com.zentu.zentu_core.notification.service;

import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.notification.client.NotificationServiceClient;
import com.zentu.zentu_core.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationServiceClient notificationServiceClient;

    public void sendNotification(
            List<String> userIds,
            List<String> groupIds,
            NotificationType notificationType,
            String template,
            Map<String, Object> context
    ){
        Map<String, Object> data  = new HashMap<>();
        data.put("user_ids", userIds);
        data.put("group_ids", groupIds);
        data.put("notification_type", notificationType.toString());
        data.put("template", template);
        data.put("context", context);
        data.put("system", "zentu");

        JsonResponse response = notificationServiceClient.sendNotification(data);
        if (!Objects.equals(response.getCode(), "200.000")){
            throw new RuntimeException(response.getMessage());
        }
    }
}
