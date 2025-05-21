package com.zentu.zentu_core.base.service;

import com.zentu.zentu_core.base.entity.Otp;
import com.zentu.zentu_core.base.repository.OtpRepository;
import com.zentu.zentu_core.notification.enums.NotificationType;
import com.zentu.zentu_core.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final NotificationService notificationService;

    public void generateOtp(String phoneNumber) {
        String otp = String.valueOf(new Random().nextInt(9000) + 1000);
        Otp otpEntity = new Otp();
        otpEntity.setPhoneNumber(phoneNumber);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpEntity);

        Map<String, Object> context = new HashMap<>();
        context.put("otp", otp);
        notificationService.sendNotification(
                null, NotificationType.SMS, phoneNumber, "sms_otp", context);
    }

    public void verifyOtp(String phoneNumber, String otp) {
        Otp otpEntity = otpRepository.findByPhoneNumberAndOtpAndVerifiedFalse(phoneNumber, otp)
                .orElseThrow(()-> new RuntimeException("Invalid Otp"));

        if (otpEntity.getExpiryTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Otp is expired");
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);
    }
}
