package com.zentu.zentu_core.notification.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.notification.enums.NotificationState;
import com.zentu.zentu_core.notification.enums.NotificationType;
import com.zentu.zentu_core.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification extends BaseEntity {
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String templateName;

    private Map<String, Object> context;

    private String responseMessage;

    private LocalDateTime sentTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationState notificationState = NotificationState.PENDING;
}
