package com.zentu.zentu_core.notification.controller;

import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.notification.dto.NotificationCallbackRequest;
import com.zentu.zentu_core.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<Void>> processNotificationCallback(
            @RequestBody @Valid NotificationCallbackRequest request) {
        notificationService.processNotificationCallback(request);
        return ResponseEntity.ok(ApiResponse.success("Callback processed successfully", null));
    }
}
