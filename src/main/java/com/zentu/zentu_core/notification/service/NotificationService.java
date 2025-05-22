package com.zentu.zentu_core.notification.service;

import com.zentu.zentu_core.notification.dto.NotificationCallbackRequest;
import com.zentu.zentu_core.notification.entity.Notification;
import com.zentu.zentu_core.notification.enums.NotificationState;
import com.zentu.zentu_core.notification.enums.NotificationType;
import com.zentu.zentu_core.notification.repository.NotificationRepository;
import com.zentu.zentu_core.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RestTemplate restTemplate;

    @Value("${notification.system-name}")
    private String systemName;

    @Value("${notification.api-url}")
    private String apiUrl;

    public void sendNotification(
            User user,
            NotificationType notificationType,
            String destination,
            String templateName,
            Map<String, Object> context) {
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .user(user)
                        .notificationType(notificationType)
                        .destination(destination)
                        .templateName(templateName)
                        .context(context)
                        .build()
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("system", systemName);
        payload.put("unique_identifier", notification.getId().toString());
        payload.put("recipients", notification.getDestination());
        payload.put("notification_type", notification.getNotificationType().name());
        payload.put("template", notification.getTemplateName());
        payload.put("context", notification.getContext());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                apiUrl,
                requestEntity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Notification not sent");
        }
    }

    public void processNotificationCallback(NotificationCallbackRequest request){
        Notification notification = notificationRepository.findById(UUID.fromString(request.getUnique_identifier()))
                .orElseThrow(()-> new RuntimeException("Notification not found"));

        try {
            notification.setNotificationState(NotificationState.valueOf(request.getStatus().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid status: " + request.getStatus());
        }

        if (request.getMessage() != null) {
            notification.setResponseMessage(request.getMessage());
        }

        if (request.getSent_time() != null) {
            notification.setSentTime(request.getSent_time());
        }

        notificationRepository.save(notification);
    }
}
