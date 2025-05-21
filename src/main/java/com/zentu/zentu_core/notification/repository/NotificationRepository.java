package com.zentu.zentu_core.notification.repository;

import com.zentu.zentu_core.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {}
